package InfoMod.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class SimpleCheckboxWidget extends ButtonWidget {
    private boolean enabled;
    private Consumer<SimpleCheckboxWidget> onEnable, onDisable;

    private static final Texture TEX_UNCHECKED = new Texture("images/widgets/checkbox.png");
    private static final Texture TEX_CHECKED = new Texture("images/widgets/checkbox_checked.png");

    public static final float WIDTH = 32.0f;
    public static final float HEIGHT = 32.0f;

    public SimpleCheckboxWidget(float x, float y, boolean startEnabled, Consumer<SimpleCheckboxWidget> onEnable, Consumer<SimpleCheckboxWidget> onDisable) {
        super(x, y, WIDTH, HEIGHT, SimpleCheckboxWidget::toggled);

        this.enabled = startEnabled;
        this.onEnable = onEnable;
        this.onDisable = onDisable;
    }

    private static void toggled(ButtonWidget buttonWidget) {
        SimpleCheckboxWidget w = (SimpleCheckboxWidget)buttonWidget;
        if (w.enabled)
            w.disable();
        else
            w.enable();
    }

    private void disable() {
        if (!enabled)
            return;

        onDisable.accept(this);
        enabled = false;
    }

    private void enable() {
        if (enabled)
            return;

        onEnable.accept(this);
        enabled = true;
    }

    public void setEnabled(boolean val) {
        if (val)
            enable();
        else
            disable();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        sb.setColor(Color.WHITE);

        if (enabled)
            sb.draw(TEX_CHECKED, x, y);
        else
            sb.draw(TEX_UNCHECKED, x, y);

        if (hasToolTip && hb.hovered) {
            TipHelper.renderGenericTip(InputHelper.mX + 40.0f, InputHelper.mY, toolTipHeader, toolTipBody);
        }

        // DEBUG
        hb.render(sb);
    }

    public boolean isEnabled() {
        return enabled;
    }

//    private static final Texture TEX_UNCHECKED = new Texture("images/checkbox_normal.png");
//    private static final Texture TEX_CHECKED = new Texture("images/checkbox_checked.png");

//    public SimpleCheckboxWidget(float x, float y, float width, float height, Consumer<ButtonWidget> onClick) {
//        super(x, y, width, height, new Texture("images/checkbox_normal"), new Texture("images/checkbox_normal"), onClick);
//    }
}
