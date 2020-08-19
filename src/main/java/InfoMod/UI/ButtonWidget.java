package InfoMod.UI;

import basemod.BaseMod;
import basemod.helpers.TooltipInfo;
import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import javax.swing.*;
import java.util.function.Consumer;

/*
  Simple hitbox based button widget. Subclasses should adjust render to draw their own textures.
 */
public abstract class ButtonWidget implements IScreenWidget, PostUpdateSubscriber {
    protected boolean visible = false;

    protected boolean hasToolTip = false;
    protected String toolTipHeader, toolTipBody;

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
            //hb.move(x + (width / 2.0f), y + (height / 2.0f));

            BaseMod.subscribe(this);
            show();
    }

    @Override
    public void show() {
        visible = true;
        hb.move(x + (width / 2.0f), y + (height / 2.0f));
    }

    @Override
    public void hide() {
        visible = false;
        hb.move(100000, 100000);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!visible)
            return;

        // TODO: subclasses should override this render and draw their own textures
        if (hasToolTip && hb.hovered) {
            //TipHelper.renderGenericTip(x, y, toolTipHeader, toolTipBody);
            TipHelper.renderGenericTip(InputHelper.mX + 40.0f, InputHelper.mY, toolTipHeader, toolTipBody);
        }

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

    public ButtonWidget with_tooltip(String header, String body) {
        hasToolTip = true;
        toolTipHeader = header;
        toolTipBody = body;
        return this;
    }
}
