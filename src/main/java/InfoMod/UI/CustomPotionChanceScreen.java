package InfoMod.UI;

import InfoMod.RenderingUtils;
import basemod.BaseMod;
import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class CustomPotionChanceScreen implements IScreen, RenderSubscriber {
    private boolean visible = false;

    private static final Texture SCREEN_BG = new Texture("images/potion_chance_settings_screen.png");
    private static final float SCREEN_W = 765.0f;
    private static final float SCREEN_H = 600.0f;

    // Layout

    // TODO: actually don't need smart text here
//    private static final float LINE_WIDTH = ;
//    private static final float LINE_SPACING = ;

    private static final float CONTENT_X = 637.0f;
    private static final float CLOSE_X = 1270.0f;

    private static final float CONFIRM_BUTTON_X = 1053.0f;
    private static final float SHORT_INFO_GAP = 26.0f;
    private static final float TEXTY_X = 880.0f;

    private static final float TITLE_TEXT_Y = 784.0f;
    private static final float MAIN_EDIT_Y = 644.0f;

    private static final float POSITION_INFO_Y = 588.0f;
    //private static final float POSITION_CONTENTS_Y = 460.0f;
    private static final float POSITION_BOTTOM_Y = 416.0f;

    //private static final float BUTTON_Y = 367.0f;
    private static final float BUTTON_Y = 275.0f;
    private static final float CLOSE_Y = 767.0f;
    private static final float RESET_Y = 687.0f;

    ColorableSmallTextButton confirmButton;//, cancelButton;
    SimpleButtonWidget cancelButton, resetButton;

    EditableTextLong mainTextArea;
    EditableNumberWidget textX, textY;

    // Input processor
    CustomInputProcessor inputProcessor;

    String cachedMainText;
    int cachedX, cachedY;


    public CustomPotionChanceScreen() {
        BaseMod.subscribe(this);
        inputProcessor = new CustomInputProcessor();

        confirmButton = new ColorableSmallTextButton(
                CONFIRM_BUTTON_X,
                BUTTON_Y,
                "Save",
                RenderingUtils.OJB_GREEN_BUTTON_COLOR,
                button -> { saveAndClose(); });

        cancelButton = ButtonFactory.buildCloseButton(
                CLOSE_X,
                CLOSE_Y,
                button -> { revertAndClose(); });

        resetButton = ButtonFactory.buildResetButton(
                CLOSE_X,
                RESET_Y,
                button -> {
                    // TODO
                    System.out.println("OJB: reset button pressed");
                    revertStashedSettings();
                });

        mainTextArea = new EditableTextLong(CONTENT_X,
                MAIN_EDIT_Y,
                "!Potions: ",
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableText -> { setFocus(editableText); },
                editableText -> { tab(); },
                editableText -> { hide(); },
                editableText -> { saveAndClose(); } );

//        textX = new EditableTextShort(CONTENT_X + SHORT_INFO_GAP,
//                POSITION_BOTTOM_Y,
//                "1494",
//                FontHelper.tipBodyFont,
//                Settings.CREAM_COLOR,
//                editableText -> { setFocus(editableText); },
//                editableText -> { tab(); },
//                editableText -> { hide(); },
//                editableText -> { saveAndClose(); } );

        textX = new EditableNumberWidget(
                CONTENT_X + SHORT_INFO_GAP,
                POSITION_BOTTOM_Y,
                0,
                1920,
                1494,
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); }
        );

        textY = new EditableNumberWidget(TEXTY_X + SHORT_INFO_GAP,
                POSITION_BOTTOM_Y,
                0,
                1080,
                1060,
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); } );

//        textY = new EditableTextShort(TEXTY_X + SHORT_INFO_GAP,
//                POSITION_BOTTOM_Y,
//                "1060",
//                FontHelper.tipBodyFont,
//                Settings.CREAM_COLOR,
//                editableText -> { setFocus(editableText); },
//                editableText -> { tab(); },
//                editableText -> { hide(); },
//                editableText -> { saveAndClose(); } );

        show();
    }

    private void tab() {
        System.out.println("OJB tabbed");

        if (mainTextArea.isFocused())
            setFocus(textX.textArea);
        else if (textX.textArea.isFocused())
            setFocus(textY.textArea);
        else
            setFocus(mainTextArea);
    }

    private void clearAllFocus() {
        mainTextArea.removeFocus();
        textX.textArea.removeFocus();
        textY.textArea.removeFocus();

        inputProcessor.reset();
    }

    private void setFocus(EditableText t) {
        clearAllFocus();
        t.setFocus();

        inputProcessor.reset();
        inputProcessor.setKeyTypedHandler(c -> t.keyTyped(c));
        inputProcessor.start();
    }

    @Override
    public void show() {
        if (visible)
            return;

        visible = true;

        RenderingUtils.openCustomScreen("DECK_OPEN");

        confirmButton.show();
        mainTextArea.show();

        textX.show();
        textY.show();

        stashCurrentSettings();
        setFocus(mainTextArea);
    }

    @Override
    public void hide() {
        if (!visible)
            return;

        inputProcessor.reset();

        RenderingUtils.closeScreens("DECK_CLOSE");

        clearAllFocus();

        confirmButton.hide();
        mainTextArea.hide();
        textX.hide();
        textY.hide();

        visible = false;
    }

    private void stashCurrentSettings() {
        cachedMainText = mainTextArea.getText();
        cachedX = textX.getValue();
        cachedY = textY.getValue();
    }

    private void revertStashedSettings() {
        mainTextArea.setText(cachedMainText);
        textX.setValue(cachedX);
        textY.setValue(cachedY);
    }

    private void saveAndClose() {
        // TODO: save / etc.
        hide();
    }

    private void revertAndClose() {
        revertStashedSettings();
        hide();
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible)
            return;

        sb.setColor(Color.WHITE);
        sb.draw( SCREEN_BG, ((float)Settings.WIDTH / 2.0f) - (SCREEN_W / 2.0f), (Settings.HEIGHT - SCREEN_H) / 2.0f );

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, "Customize the text displayed by the Potion Tracker.", CONTENT_X, TITLE_TEXT_Y, Settings.GOLD_COLOR);
        FontHelper.renderSmartText(sb, FontHelper.tipHeaderFont, "Customize the positioning of the text. NL (0,0) bottom left -> (1920, 1080) top right", CONTENT_X, POSITION_INFO_Y,  Settings.GOLD_COLOR);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "x", CONTENT_X, POSITION_BOTTOM_Y + EditableTextShort.TEXT_OFFSET_Y, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipBodyFont, "y", TEXTY_X, POSITION_BOTTOM_Y + EditableTextShort.TEXT_OFFSET_Y, Settings.CREAM_COLOR);

        // Render the IScreenWidgets
        mainTextArea.render(sb);
        confirmButton.render(sb);
        cancelButton.render(sb);
        resetButton.render(sb);

        textX.render(sb);
        textY.render(sb);
    }
}
