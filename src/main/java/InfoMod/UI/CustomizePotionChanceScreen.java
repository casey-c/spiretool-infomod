package InfoMod.UI;

import InfoMod.Config;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class CustomizePotionChanceScreen extends MediumLabeledScreen {
    private SimpleButtonWidget resetButton;
    private EditableTextLong mainTextArea;
    private EditableNumberWidget textX, textY;

    CustomInputProcessor inputProcessor;
    String cachedMainText;
    int cachedX, cachedY;

    private static final float HELP_SIZE = 30.0f;

    public CustomizePotionChanceScreen(String title) {
        super(title);
        cancelButton.with_tooltip("Cancel", "Revert all changes and close this popup.");

        inputProcessor = new CustomInputProcessor();

        resetButton = (SimpleButtonWidget) ButtonFactory.buildResetButton(
                ABSOLUTE_CONTENT_RIGHT_X - CONTENT_PADDING_SM - CLOSE_BUTTON_SIZE,
                ABSOLUTE_CONTENT_TITLE_TOP_Y - 2.0f * CONTENT_PADDING_SM - 2.0f * CLOSE_BUTTON_SIZE,
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

        float MAIN_TEXT_AREA_Y = ABSOLUTE_CONTENT_BODY_TOP_Y - CONTENT_PADDING_MD - HELP_SIZE - CONTENT_PADDING_SM - EditableTextLong.HEIGHT;

        mainTextArea = new EditableTextLong(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD,
                MAIN_TEXT_AREA_Y,
                //SaveableManager.potionChanceCustom.DEFAULT_TEXT,
                //SaveableManager.potionChanceCustom.customText,
                //ConfigHelper.getInstance().getString(ConfigHelper.StringSettings.POTION_CHANCE),
                Config.getString(Config.ConfigOptions.POTION_TEXT),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableText -> { setFocus(editableText); },
                editableText -> { tab(); },
                editableText -> { hide(); },
                editableText -> { saveAndClose(); },
                editableText -> {
                    Config.setString(Config.ConfigOptions.POTION_TEXT, editableText.text);
                    //ConfigHelper.getInstance().setString(ConfigHelper.StringSettings.POTION_CHANCE, editableText.text);
                    //SaveableManager.potionChanceCustom.customText = editableText.text;
                }
        );

        float TEXT_Y = MAIN_TEXT_AREA_Y - CONTENT_PADDING_MD - HELP_SIZE - CONTENT_PADDING_SM - EditableTextShort.HEIGHT;
        textX = new EditableNumberWidget(
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD,
                TEXT_Y,
                0,
                1920,
                //1494,
                //SaveableManager.potionChanceCustom.DEFAULT_X,
                //SaveableManager.potionChanceCustom.x,
                //ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_X),
                Config.getInt(Config.ConfigOptions.POTION_X),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_X, editableText.getValue());
                    //ConfigHelper.getInstance().setInt(ConfigHelper.IntSettings.POTION_X, editableText.getValue());
                    //SaveableManager.potionChanceCustom.x = editableText.getValue();
                }
        );

        float TEXT_WIDGET_GAP = 300.0f;

        textY = new EditableNumberWidget(
                //TEXTY_X + SHORT_INFO_GAP,
                ABSOLUTE_CONTENT_LEFT_X + CONTENT_PADDING_MD + TEXT_WIDGET_GAP,
                TEXT_Y,
                0,
                1080,
                //1060,
                //SaveableManager.potionChanceCustom.DEFAULT_Y,
                //SaveableManager.potionChanceCustom.y,
                //ConfigHelper.getInstance().getInt(ConfigHelper.IntSettings.POTION_Y),
                Config.getInt(Config.ConfigOptions.POTION_Y),
                FontHelper.tipBodyFont,
                Settings.CREAM_COLOR,
                editableNumberWidget -> { setFocus(editableNumberWidget.textArea); },
                editableNumberWidget -> { tab(); },
                editableNumberWidget -> { hide(); },
                editableNumberWidget -> { saveAndClose(); },
                editableText -> {
                    Config.setInt(Config.ConfigOptions.POTION_Y, editableText.getValue());
                    //ConfigHelper.getInstance().setInt(ConfigHelper.IntSettings.POTION_Y, editableText.getValue());
                    //SaveableManager.potionChanceCustom.y = editableText.getValue();
                }
        );

        childWidgets.add(mainTextArea);
        childWidgets.add(textX);
        childWidgets.add(textY);
        childWidgets.add(resetButton);
    }

    private void revertToModDefaults() {
        mainTextArea.setText(Config.getDefaultString(Config.ConfigOptions.POTION_TEXT));
        textX.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_X));
        textY.setValue(Config.getDefaultInt(Config.ConfigOptions.POTION_Y));
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
}
