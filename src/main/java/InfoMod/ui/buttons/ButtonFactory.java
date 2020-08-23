package InfoMod.ui.buttons;

import com.badlogic.gdx.graphics.Texture;

import java.util.function.Consumer;

public class ButtonFactory {
    public static SimpleButtonWidget buildCloseButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 50.0f, 50.0f, new Texture("images/widgets/button_close.png"), new Texture("images/widgets/button_close_hovered.png"), onClick);
    }

    public static SimpleButtonWidget buildResetButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 50.0f, 50.0f, new Texture("images/widgets/button_reset.png"), new Texture("images/widgets/button_reset_hovered.png"), onClick);
    }

    public static SimpleButtonWidget buildPlusButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 20.0f, 20.0f, new Texture("images/button_plus.png"), new Texture("images/button_plus_hover.png"), onClick);
    }

    public static SimpleButtonWidget buildMinusButton(float x, float y, Consumer<ButtonWidget> onClick) {
        return new SimpleButtonWidget(x, y, 20.0f, 20.0f, new Texture("images/button_minus.png"), new Texture("images/button_minus_hover.png"), onClick);
    }
}
