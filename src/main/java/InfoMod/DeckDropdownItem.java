package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import java.util.ArrayList;

// This class overrides the original Deck TopPanelItem's dropdown tip to let the mod update with current cards.
//
// The original is unchanged, but this item has a hitbox that covers up the original, letting us easily customize
// what the tip contains without needing to worry about patching existing code.
public class DeckDropdownItem implements PostRenderSubscriber, HitboxListener, PostInitializeSubscriber {

    private Hitbox hb;

    private float TIP_X = 0.0f;
    private float TIP_Y = 0.0f;

    private String tip_body;

    public DeckDropdownItem() {
        BaseMod.subscribe(this);

        // Note: 67x67 should be big enough to cover the 64x64 original icon
        hb = new Hitbox(67.0f, 67.0f);
    }

    @Override
    public void receivePostInitialize() {
        // Need access to Settings vars, so called after initializing
        float cx = (float)Settings.WIDTH - 117.0f * Settings.scale;
        float cy = (float)Settings.HEIGHT - 33.0f * Settings.scale;

        hb.move(cx, cy);

        TIP_X = 1550.0f * Settings.scale;
        TIP_Y = (float) Settings.HEIGHT - 120.0F * Settings.scale;

        tip_body = "TODO";
    }

    public void setString(String s) {
        tip_body = s;
    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
        // DEBUG: show the hitbox
        //hb.render(spriteBatch);

        hb.encapsulatedUpdate(this);

        if (AbstractDungeon.isPlayerInDungeon() && hb.hovered) {
            TipHelper.renderGenericTip(TIP_X, TIP_Y, "Current Deck", tip_body);
            // TODO: maybe queue up multiple tips, so we have the original info as well
        }
    }

    @Override
    public void hoverStarted(Hitbox hitbox) {

        if (hitbox == hb) {
            System.out.println("OJB: hb");
        }
        else {
            System.out.println("OJB: not hb");
        }
    }


    @Override
    public void startClicking(Hitbox hitbox) {

    }

    @Override
    public void clicked(Hitbox hitbox) {

    }

}
