package InfoMod;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class StringInputListener implements TextField.TextFieldListener {
    @Override
    public void keyTyped(TextField textField, char c) {
        System.out.println("OJB: string input listener typed: " + c);
        textField.appendText("" + c);
    }
}
