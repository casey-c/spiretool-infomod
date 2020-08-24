package InfoMod.ui.screens;

import InfoMod.ui.IScreenWidget;
import InfoMod.utils.RenderingUtils;
import InfoMod.utils.ScreenHelper;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;

public abstract class AbstractScreen implements RenderSubscriber {
    protected boolean visible;
    protected ArrayList<IScreenWidget> childWidgets;

    protected Texture SCREEN_BG;
    protected float SCREEN_W, SCREEN_H;

    public AbstractScreen() {
        this.childWidgets = new ArrayList<>();
        this.visible = false;

        BaseMod.subscribe(this);
    }

//    private static final Texture SCREEN_BG = new Texture("images/screens/medium_labeled.png");
//    private static final float SCREEN_W = 765.0f;
//    private static final float SCREEN_H = 600.0f;

    public boolean isVisible() {
        return visible;
    }

    public void show() {
        if (visible)
            return;

        ScreenHelper.openCustomScreen("DECK_OPEN");

        for (IScreenWidget w : childWidgets)
            w.show();

        visible = true;
    }

    public void hide() {
        if (!visible)
            return;

        ScreenHelper.closeCustomScreen("DECK_CLOSE");

        for (IScreenWidget w : childWidgets)
            w.hide();

        visible = false;
    }

    public void saveAndClose() {
        hide();
        // TODO: override as needed
    }

    public void revertAndClose() {
        hide();
        // TODO: override as needed
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!visible)
            return;

        // Render the screen background
        sb.setColor(Color.WHITE);
        sb.draw(SCREEN_BG,
                ((float) Settings.WIDTH - SCREEN_W * Settings.scale) * 0.5f,
                ((float) Settings.HEIGHT - SCREEN_H * Settings.scale) * 0.5f
        );

        for (IScreenWidget w : childWidgets)
            w.render(sb);
    }
}
