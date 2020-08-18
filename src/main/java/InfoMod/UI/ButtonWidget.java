package InfoMod.UI;

import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

/*
  Simple hitbox based button widget. Subclasses should adjust render to draw their own textures.
 */
public abstract class ButtonWidget implements IScreenWidget, PostUpdateSubscriber {
    protected boolean visible = false;

    protected float x, y;
    protected float width, height;
    protected Hitbox hb;


    protected Consumer<ButtonWidget> onClick;

    public ButtonWidget(float x, float y, float width, float height, Consumer<ButtonWidget> onClick) {
            this.x = x;
            this.y = y;

            this.width = width;
            this.height = height;

            this.onClick = onClick;

            hb = new Hitbox(width, height);
            hb.move(x + (width / 2.0f), y + (height / 2.0f));

            BaseMod.subscribe(this);
            show();
    }

    @Override
    public void show() {
        visible = true;
    }

    @Override
    public void hide() {
        visible = false;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        // TODO: subclasses should override this render and draw their own textures

        // DEBUG
        hb.render(sb);

    }

    @Override
    public void receivePostUpdate() {
        if (!visible)
            return;

        hb.update();

        // Update the hitbox
        if (hb.justHovered)
            CardCrawlGame.sound.play("UI_HOVER");

        if (InputHelper.justClickedLeft && hb.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            hb.clickStarted = true;
        }

        if (hb.clicked) {
            hb.clicked = false;

            // Handle
            onClick.accept(this);
        }


    }
}
