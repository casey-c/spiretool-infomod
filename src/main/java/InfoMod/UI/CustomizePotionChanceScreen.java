package InfoMod.UI;

import InfoMod.Config;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class CustomizePotionChanceScreen extends MediumLabeledScreen {
    private SimpleButtonWidget resetButton;
    private EditableTextLong mainTextArea;
    private EditableNumberWidget textX, textY;

    private SimpleToolTipWidget mainHelpTip, positionHelpTip;
    private SimpleCheckboxWidget checkboxShowImage;

    private CustomInputProcessor inputProcessor;
    private String cachedMainText;
    private int cachedX, cachedY;
    private boolean cachedCheckbox;

    private static final float HELP_SIZE = 30.0f;
    private static final float ICON_TEXT_OFFSET = 400.0f;

    public CustomizePotionChanceScreen() {
        super("Potion Tracker Settings");
        cancelButton.with_tooltip("Cancel", "Revert all changes and close this popup.");

        inputProcessor = new CustomInputProcessor();

        resetButton = (SimpleButtonWidget) ButtonFactory.buildResetButton(
                ABSOLUTE_CONTENT_RIGHT_X - CONTENT_PADDING_SM - CLOSE_BUTTON_SIZE,
                ABSOLUTE_CONTENT_TITLE_TOP_Y - 2.0f * CONTENT_PADDING_SM - 2.0f * CLOSE_BUTTON_SIZE,
                button -> {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
                        revertToModDefaults();
                    else
                        revertStashedSettings();
                })
                .with_tooltip("Reset", "Resets all settings in this dialog to their initial values. NL NL Hold #bSHIFT to reset everything to the mod defaults.");

        float MAIN_TEXT_AREA_Y = ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD - HELP_SIZE - CONTENT_PADDING_SM - EditableTextLong.HEIGHT;

        mainTextArea = new EditableTextLong(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD,
                MAIN_TEXT_AREA_Y,
                Config.getString(Config.ConfigOptions.POTION_TEXT),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableText -> { setFocus(editableText); },
                editableText -> { tab(); },
                editableText -> { hide(); },
                editableText -> { saveAndClose(); },
                editableText -> {
                    Config.setString(Config.ConfigOptions.POTION_TEXT, editableText.text);
                }
        );

        float TEXT_Y = MAIN_TEXT_AREA_Y - CONTENT_PADDING_MD - HELP_SIZE - CONTENT_PADDING_SM - EditableTextShort.HEIGHT;
        textX = new EditableNumberWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD,
                TEXT_Y,
                0,
                1920,
                Config.getInt(Config.ConfigOptions.POTION_X),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_X, editableText.getValue());
                }
        );

        float TEXT_WIDGET_GAP = 300.0f;

        textY = new EditableNumberWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD + TEXT_WIDGET_GAP,
                TEXT_Y,
                0,
                1080,
                Config.getInt(Config.ConfigOptions.POTION_Y),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_Y, editableText.getValue());
                }
        );

        float HELP_OFFSET = 7.0f;

        mainHelpTip = new SimpleToolTipWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_SM,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD - HELP_SIZE + HELP_OFFSET,
                HELP_SIZE,
                HELP_SIZE,
                new Texture("images/help_tip.png"),
                "Text Help",
                "You can use special color formatting built into the base game. These are two character pairs starting with the pound / hash symbol (Shift+3 on the standard keyboard), and then one of the following combinations: NL NL #rr=Red, #bb=Blue, #gg=Green, #pp=Purple, #yy=Yellow. NL NL Putting colors on words requires this formatting before each word in the sentence, as it will only apply color up to the space character."
        );

        positionHelpTip = new SimpleToolTipWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_SM,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD - HELP_SIZE
                        - CONTENT_PADDING_SM - EditableTextLong.HEIGHT
                        - CONTENT_PADDING_MD - HELP_SIZE
                        + HELP_OFFSET,
                HELP_SIZE,
                HELP_SIZE,
                new Texture("images/help_tip.png"),
                "Position Help",
                "The bottom left of the screen is (0, 0), while the top right is (1920, 1080). These (x,y) coordinates will point to the top left most point of the text. NL NL You can finely adjust the values using the plus/minus buttons next to the input. NL NL Hold #bShift while clicking these buttons to adjust by +10 or -10. Hold #bCTRL+SHIFT while clicking to adjust by +100 or -100."
        );

        // Checkbox
        float CHECKBOX_OFFSET_Y = 22.0f;
        checkboxShowImage = new SimpleCheckboxWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD + EditableTextLong.WIDTH - CONTENT_PADDING_SM - SimpleCheckboxWidget.WIDTH,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CHECKBOX_OFFSET_Y - SimpleCheckboxWidget.HEIGHT,
                Config.getBool(Config.ConfigOptions.SHOW_POTION_IMG),
                simpleCheckboxWidget -> {
                    Config.setBoolean(Config.ConfigOptions.SHOW_POTION_IMG, true);
                },
                simpleCheckboxWidget -> {
                    Config.setBoolean(Config.ConfigOptions.SHOW_POTION_IMG, false);
                }
        );

        childWidgets.add(checkboxShowImage);


        childWidgets.add(mainTextArea);
        childWidgets.add(textX);
        childWidgets.add(textY);
        childWidgets.add(resetButton);

        childWidgets.add(mainHelpTip);
        childWidgets.add(positionHelpTip);
    }

    private void revertToModDefaults() {
        mainTextArea.setText(Config.getDefaultString(Config.ConfigOptions.POTION_TEXT));
        textX.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_X));
        textY.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_Y));
        checkboxShowImage.setEnabled(Config.getDefaultBool(Config.ConfigOptions.SHOW_POTION_IMG));
    }

    private void stashCurrentSettings() {
        cachedMainText = mainTextArea.getText();
        cachedX = textX.getValue();
        cachedY = textY.getValue();
        cachedCheckbox = checkboxShowImage.isEnabled();
    }

    private void revertStashedSettings() {
        mainTextArea.setText(cachedMainText);
        textX.setValue(cachedX);
        textY.setValue(cachedY);
        checkboxShowImage.setEnabled(cachedCheckbox);
    }

    @Override
    public void revertAndClose() {
        revertStashedSettings();
        hide();
    }

    private void tab() {
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

        stashCurrentSettings();
        setFocus(mainTextArea);

        super.show();
    }

    @Override
    public void hide() {
        super.hide();
        clearAllFocus();
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        super.receiveRender(sb);

        if (!visible)
            return;

        if (!CardCrawlGame.isInARun()) {
            revertAndClose();
            return;
        }

        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SETTINGS) {
            revertAndClose();
            // will need something a bit more complicated than the following, so for now just hide the settings window
            //AbstractDungeon.screen = AbstractDungeon.CurrentScreen.SETTINGS;
            return;
        }

        sb.setColor(Color.WHITE);

        FontHelper.renderFontLeftTopAligned(
                sb,
                FontHelper.tipBodyFont,
                "Customize the text displayed.",
                ABSOLUTE_CONTENT_LEFT_X + HELP_SIZE + CONTENT_PADDING_MD,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD,
                Settings.GOLD_COLOR
                );

        FontHelper.renderFontLeftTopAligned(
                sb,
                FontHelper.tipBodyFont,
                "Show Icon: ",
                ABSOLUTE_CONTENT_LEFT_X + HELP_SIZE + CONTENT_PADDING_MD + ICON_TEXT_OFFSET,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD,
                Settings.GOLD_COLOR
        );

        FontHelper.renderFontLeftTopAligned(
                sb,
                FontHelper.tipBodyFont,
                "Customize the positioning of the text.",
                ABSOLUTE_CONTENT_LEFT_X + HELP_SIZE + CONTENT_PADDING_MD,
                ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD - HELP_SIZE - CONTENT_PADDING_SM - EditableTextLong.HEIGHT - CONTENT_PADDING_MD,
                Settings.GOLD_COLOR
        );

    }
}
