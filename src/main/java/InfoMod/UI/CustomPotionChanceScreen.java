package InfoMod.UI;

import InfoMod.Config;
import InfoMod.RenderingUtils;
import InfoMod.SaveableManager;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

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

    //private static final float CONFIRM_BUTTON_X = 1053.0f;
    private static final float CONFIRM_BUTTON_X = 1088.0f;

    private static final float SHORT_INFO_GAP = 26.0f;

    //private static final float TEXTY_X = 880.0f;
    private static final float TEXTY_X = 920.0f;

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

    ArrayList<IScreenWidget> childWidgets = new ArrayList<>();

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
                21.0f,
                -2.0f,
                "Save",
                RenderingUtils.OJB_GREEN_BUTTON_COLOR,
                button -> { saveAndClose(); });

        cancelButton = (SimpleButtonWidget) ButtonFactory.buildCloseButton(
                CLOSE_X,
                CLOSE_Y,
                button -> { revertAndClose(); })
                .with_tooltip("Cancel", "Revert all changes and close this popup.");

        resetButton = (SimpleButtonWidget) ButtonFactory.buildResetButton(
                CLOSE_X,
                RESET_Y,
                button -> {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                        System.out.println("OJB: shift is pressed");

                        // TODO: revert to InfoMod default values using some sort of Properties thingy like we save with
                        //revertStashedSettings();
                        revertToModDefaults();
                        // remember to update the tool tip when you get to this, future me
                    }
                    else {
                        revertStashedSettings();
                    }

                })
                .with_tooltip("Reset", "Resets all settings in this dialog to their initial values. NL NL Hold #bSHIFT to reset everything to the mod defaults.");

        mainTextArea = new EditableTextLong(CONTENT_X,
                MAIN_EDIT_Y,
                //SaveableManager.potionChanceCustom.DEFAULT_TEXT,
                //SaveableManager.potionChanceCustom.customText,
                //ConfigHelper.getInstance().getString(ConfigHelper.StringSettings.POTION_CHANCE),
                Config.getString(Config.ConfigOptions.POTION_TEXT.toString()),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableText -> { setFocus(editableText); },
                editableText -> { tab(); },
                editableText -> { hide(); },
                editableText -> { saveAndClose(); },
                editableText -> {
                    Config.setString(Config.ConfigOptions.POTION_TEXT.toString(), editableText.text);
                    //ConfigHelper.getInstance().setString(ConfigHelper.StringSettings.POTION_CHANCE, editableText.text);
                    //SaveableManager.potionChanceCustom.customText = editableText.text;
                }
                );

        textX = new EditableNumberWidget(
                CONTENT_X + SHORT_INFO_GAP,
                POSITION_BOTTOM_Y,
                0,
                1920,
                //1494,
                //SaveableManager.potionChanceCustom.DEFAULT_X,
                //SaveableManager.potionChanceCustom.x,
                //ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_X),
                Config.getInt(Config.ConfigOptions.POTION_X.toString()),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_X.toString(), editableText.getValue());
                    //ConfigHelper.getInstance().setInt(ConfigHelper.IntSettings.POTION_X, editableText.getValue());
                    //SaveableManager.potionChanceCustom.x = editableText.getValue();
                }
        );

        textY = new EditableNumberWidget(TEXTY_X + SHORT_INFO_GAP,
                POSITION_BOTTOM_Y,
                0,
                1080,
                //1060,
                //SaveableManager.potionChanceCustom.DEFAULT_Y,
                //SaveableManager.potionChanceCustom.y,
                //ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_Y),
                Config.getInt(Config.ConfigOptions.POTION_Y.toString()),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_Y.toString(), editableText.getValue());
                    //ConfigHelper.getInstance().setInt(ConfigHelper.IntSettings.POTION_Y, editableText.getValue());
                    //SaveableManager.potionChanceCustom.y = editableText.getValue();
                }
                );

        childWidgets.add(mainTextArea);
        childWidgets.add(textX);
        childWidgets.add(textY);
        childWidgets.add(cancelButton);
        childWidgets.add(confirmButton);
        childWidgets.add(resetButton);
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

        for (IScreenWidget w : childWidgets)
            w.show();

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

        for (IScreenWidget w : childWidgets)
            w.hide();

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

    private void revertToModDefaults() {
//        mainTextArea.setText(SaveableManager.potionChanceCustom.DEFAULT_TEXT);
//        textX.setValue(SaveableManager.potionChanceCustom.DEFAULT_X);
//        textY.setValue(SaveableManager.potionChanceCustom.DEFAULT_Y);
//
//        mainTextArea.setText(ConfigHelper.getInstance().getDefaultString(ConfigHelper.StringSettings.POTION_CHANCE));
//        textX.setValue(ConfigHelper.getInstance().getDefaultInt(ConfigHelper.IntSettings.POTION_X));
//        textY.setValue(ConfigHelper.getInstance().getDefaultInt(ConfigHelper.IntSettings.POTION_Y));

        mainTextArea.setText(Config.getDefaultString(Config.ConfigOptions.POTION_TEXT.toString()));
        textX.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_X.toString()));
        textY.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_Y.toString()));
    }

    private void saveAndClose() {
        // TODO: save / etc.
        hide();
    }

    public void revertAndClose() {
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
