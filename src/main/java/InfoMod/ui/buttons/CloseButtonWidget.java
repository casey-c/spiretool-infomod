package InfoMod.ui.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

/*
TODO: tbh, this should just be abstracted out as a SimpleButtonWidget or something, with the params passed as args
  however, this is super convienient currently.

TODO: a ButtonFactory could maybe expose a .buildCloseButton()  to keep the convienience
public class CloseButtonWidget extends ButtonWidget {
    private static final float BUTTON_WIDTH = 50.0f;
    private static final float BUTTON_HEIGHT = 50.0f;

    private static final Texture TEX_NORMAL = new Texture("images/x_button.png");
    private static final Texture TEX_PRESSED = new Texture("images/x_button_pressed.png");

    public CloseButtonWidget(float x, float y, Consumer<ButtonWidget> onClick) {
        super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, onClick);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if (!visible)
            return;

        sb.setColor(Color.WHITE);

        if (hb.hovered)
            sb.draw(TEX_PRESSED, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        else
            sb.draw(TEX_NORMAL, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
}
 */
