package InfoMod.UI;

import com.badlogic.gdx.graphics.Texture;

import java.util.function.Consumer;

public class ButtonFactory {
    static SimpleButtonWidget buildCloseButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 50.0f, 50.0f, new Texture("images/x_button.png"), new Texture("images/x_button_pressed.png"), onClick);
    }

    static SimpleButtonWidget buildResetButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 50.0f, 50.0f, new Texture("images/button_reset.png"), new Texture("images/button_reset_hover.png"), onClick);
    }

    static SimpleButtonWidget buildPlusButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 20.0f, 20.0f, new Texture("images/button_plus.png"), new Texture("images/button_plus_hover.png"), onClick);
    }

    static SimpleButtonWidget buildMinusButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 20.0f, 20.0f, new Texture("images/button_minus.png"), new Texture("images/button_minus_hover.png"), onClick);
    }
}
