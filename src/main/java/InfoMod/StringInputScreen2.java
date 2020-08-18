package InfoMod;

import InfoMod.UI.ColorableSmallTextButton;
import InfoMod.UI.EditableText;
import InfoMod.UI.EditableTextLong;
import InfoMod.UI.IScreen;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class StringInputScreen2 implements IScreen, RenderSubscriber {
    private boolean visible = false;

    TextField textField;
    InputProcessor oldInputProcessor;

    ColorableSmallTextButton button;
    EditableTextLong mainTextArea;

    public StringInputScreen2() {
        textField = new TextField(
                "!Potions: ",
                new TextField.TextFieldStyle(FontHelper.tipBodyFont, Settings.CREAM_COLOR,
                        null, null, null));
        textField.setBounds(100.0f, 100.0f, 300.0f, 40.0f);

        // testing
        button = new ColorableSmallTextButton(500.0f,
                500.0f,
                "Confirm",
                RenderingUtils.OJB_RED_BUTTON_COLOR,
                button -> {
                    // TODO: other stuff
                    mainTextArea.removeFocus();
                });

//        mainTextArea = new EditableTextLong(637.0f,
//                747.0f,
//                "!Potions: ",
//                FontHelper.tipBodyFont,
//                Settings.CREAM_COLOR,
//                editableText -> {
//                    // TODO: clear other focuses first!
//                    editableText.removeFocus();
//
//                    // TODO: set this as the target for the text updates
//                },
//                editableText -> { tab(); }
//
//                );

        // won't work (not sure how to sign up and connect this listener to the event loop)
//        textField.setTextFieldListener(new TextField.TextFieldListener() {
//            @Override
//            public void keyTyped(TextField textField, char c) {
//                System.out.println("OJB: textField listener got an event: " + c);
//            }
//        });


        //textField.setDisabled(false);
        //InputListener tfl = textField.getDefaultInputListener();
        //tfl.
        //textField.setTextFieldListener(new StringInputListener());

        BaseMod.subscribe(this);
    }

    public boolean isVisible() { return visible; }
    public void show() {
        System.out.println("OJB: show string input screen");

        oldInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(new StringInputProcessor(this, textField));

        RenderingUtils.openCustomScreen("DECK_OPEN");
        //Gdx.input.setInputProcessor();

        // TODO:
        button.show();
        mainTextArea.show();

        visible = true;
    }
    public void hide() {
        System.out.println("OJB: hide string input screen");

        Gdx.input.setInputProcessor(oldInputProcessor);
        RenderingUtils.closeScreens("DECK_CLOSE");

        // TODO:
        button.hide();
        mainTextArea.hide();

        visible = false;
    }


    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible)
            return;

        sb.setColor(Color.WHITE);
        sb.draw(
                //ImageMaster.OPTION_CONFIRM,
                ImageMaster.WHITE_SQUARE_IMG,
                (float)Settings.WIDTH / 2.0F - 300.0F, //(float)Settings.WIDTH / 2.0F - 180.0F,
                Settings.OPTION_Y - 207.0F,
                180.0F,
                207.0F,
                600.0F, //360.0F,
                414.0F, //414.0F,
                Settings.scale,
                Settings.scale,
                0.0F,
                0,
                0,
                360,
                414,
                false,
                false);

        // Draw the text field
        textField.draw(sb, 1.0f);

        // Draw the button
        button.render(sb);

        // Draw the main edit area textbox
        mainTextArea.render(sb);

    }
}
