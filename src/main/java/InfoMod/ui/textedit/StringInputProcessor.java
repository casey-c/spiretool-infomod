package InfoMod.ui.textedit;
/*

import InfoMod.InfoMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class StringInputProcessor implements InputProcessor {
    private StringInputScreen2 parent;
    private TextField textField;
    private TextField.TextFieldListener tfl;

    private float waitTimer = 0.0f;
    private static final float KEY_DOWN_DELAY = 0.09f;

    public StringInputProcessor(StringInputScreen2 parent, TextField textField) {
        this.parent = parent;
        this.textField = textField;

        this.tfl = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                System.out.println("OJB: tfl listener keyTyped");
                textField.appendText("" + c);
                System.out.println(textField.getText().length() + " LENGTH TEXT FIELD CONTENTS: '" + textField.getText() + "'");

                if (c == '\b') {
                    System.out.println("\ttfl: backslash b backspace");

                    // TODO: add delay?

                    String t = textField.getText();
                    if (t.length() > 0)
                        textField.setText(t.substring(0, t.length() - 1));

                }
                else if (c == '\r') {
                    System.out.println("\ttfl: enter");
                    parent.hide();
                }

            }
        };

        textField.setTextFieldListener(tfl);
    }

    private boolean validChar(char c) {
//        if (Character.isLetterOrDigit(c))
//            return true;
//        else
//            return false;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE)
            parent.hide();

//        System.out.println("OJB: keyDown: " + keycode);
//        System.out.println("OJB: wait timer on backspace = " + waitTimer);

//        if (keycode == Input.Keys.BACKSPACE && waitTimer <= 0.0f) {
//            System.out.println("OJB: BACKSPACE");
//
//            String t = textField.getText();
//            if (t.length() > 0)
//                textField.setText(t.substring(0, t.length() - 1));
//
//            waitTimer = KEY_DOWN_DELAY;
//            return true;
//        }
//
//        // Subtract from the delay time
//        if (this.waitTimer > 0.0F) {
//            this.waitTimer -= Gdx.graphics.getDeltaTime();
//            System.out.println("OJB: wait timer decreased to: " + waitTimer);
//        }

        // Enter confirms the selection and closes the screen
//        if (keycode == Input.Keys.ENTER) {
//            System.out.println("OJB: CONFIRM");
//            parent.hide();
//            return true;
//        }

        // TODO: escape?

        return false;
//        System.out.println("OJB: keydown and key was " + keycode);
//
//        String tmp = Input.Keys.toString(keycode);
//        if (tmp.equals("Space") && tmp.length() != 0) {
//            //ModTextPanel.textField = ModTextPanel.textField + ' ';
//            parent.appendLetter(' ');
//
//
//            return false;
//        } else if (tmp.length() != 1) {
//            return false;
//        } else if (FontHelper.getSmartWidth(FontHelper.cardTitleFont_small,
//                parent.getText(),
//                1.0E7F,
//                0.0F) >= 240.0F * Settings.scale) {
//            return false;
//        } else {
//            //Input.Keys.SHIFT_LEFT || Input.Keys.SHIFT_RIGHT
//            if (!Gdx.input.isKeyPressed(59) && !Gdx.input.isKeyPressed(60)) {
//                tmp = tmp.toLowerCase();
//            }
//
//            char c = tmp.charAt(0);
//            //if (Character.isDigit(tmp2) || Character.isLetter(tmp2)) {
//            if (validChar(c))
//                parent.appendLetter(c);
//                //ModTextPanel.textField = ModTextPanel.textField + tmp2;
//            //}
//
//            return true;
//        }
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        //parent.appendLetter(c);
        //return true;
        System.out.println("OJB keyTyped in the processor, giving it to tfl: " + c);
        tfl.keyTyped(textField, c);
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}

 */
