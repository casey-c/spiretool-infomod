package InfoMod.UI;

import basemod.BaseMod;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class SimpleToolTipWidget implements IScreenWidget, PostUpdateSubscriber {
    protected boolean visible = false;

    protected Hitbox hb;
    protected float x, y;
    protected float width, height;

    protected Texture texture;
    protected String tipHeader, tipBody;

    public SimpleToolTipWidget(float x, float y, float width, float height, Texture tex, String header, String body) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.texture = tex;
        this.tipHeader = header;
        this.tipBody = body;

        hb = new Hitbox(width, height);

        BaseMod.subscribe(this);
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

        sb.setColor(Color.WHITE);
        sb.draw(texture, x, y);

        if (hb.hovered) {
            //TipHelper.renderGenericTip(x, y, toolTipHeader, toolTipBody);
            TipHelper.renderGenericTip(InputHelper.mX + 40.0f, InputHelper.mY, tipHeader, tipBody);
        }

        // DEBUG
        hb.render(sb);

    }

    @Override
    public void receivePostUpdate() {
        if (!visible)
            return;

        hb.update();

//        if (hb.justHovered)
//            CardCrawlGame.sound.play("UI_HOVER");

//        if (InputHelper.justClickedLeft && hb.hovered) {
//            CardCrawlGame.sound.play("UI_CLICK_1");
//            hb.clickStarted = true;
//        }

//        if (hb.clicked) {
//            hb.clicked = false;
//
//            // Handle
//            onClick.accept(this);
//        }

    }
}
