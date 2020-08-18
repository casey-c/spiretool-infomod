package InfoMod.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

public class SimpleButtonWidget extends ButtonWidget {
    protected Texture TEX_NORMAL;
    protected Texture TEX_PRESSED;

    public SimpleButtonWidget(float x, float y, float width, float height, Texture normal, Texture pressed, Consumer<ButtonWidget> onClick) {
        super(x, y, width, height, onClick);

        this.TEX_NORMAL = normal;
        this.TEX_PRESSED = pressed;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if (!visible)
            return;

        sb.setColor(Color.WHITE);

        if (hb.hovered)
            sb.draw(TEX_PRESSED, x, y, width, height);
        else
            sb.draw(TEX_NORMAL, x, y, width, height);
    }
}
