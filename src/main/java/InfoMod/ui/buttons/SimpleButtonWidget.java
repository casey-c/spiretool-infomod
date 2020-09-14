package InfoMod.ui.buttons;

import InfoMod.ui.buttons.ButtonWidget;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

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

        if (hb.hovered) {
            sb.draw(TEX_PRESSED,
                    x * Settings.scale,
                    y * Settings.scale,
                    width * Settings.scale,
                    height * Settings.scale);
        } else {
            sb.draw(TEX_NORMAL,
                    x * Settings.scale,
                    y * Settings.scale,
                    width * Settings.scale,
                    height * Settings.scale);
        }
    }
}
