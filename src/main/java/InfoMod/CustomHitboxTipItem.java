package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.ArrayList;

/*
  This is a helper class to add a general hitbox/tip item combo to an arbitrary position on screen. The default BaseMod
    behavior abstracts / hide the hitbox too much, so this allows more generally setting up a hitbox / tip combo. You
    can extend this class to add additional tips by modifying the tips arraylist. Take note to update the slayTheRelics
    integration as well.

  NOTE:
    Average use cases will simply be a SCALED_FROM_UPPER_RIGHT/LEFT, TOP_CENTERED_UNDER_HB, with setPrimaryTipBoth being
    called to set / update the tool tip as needed

  The constructor sets up the places to move the hitbox/tips on screen but doesn't perform the move until the PostInit
    (since many uses require the Settings.width/height/etc. and they are zerod until after that callback fires) The
    POS_TYPE enums help with alignment, though the absolute type is provided for moving it arbirarily (you should
    set the scale on the other end in this case!).

  NOTE for derived classes:
    Setting the usePrimaryTip to false will disable rendering the default tip so you can just write your own
    implementation as needed
 */

@SpireInitializer
public class CustomHitboxTipItem implements PostRenderSubscriber, HitboxListener, PostInitializeSubscriber {
    // For setting the hitbox position in the constructor
    public enum HB_POS_TYPE {
        ABSOLUTE,
        SCALED_FROM_BOTTOM_LEFT,
        SCALED_FROM_BOTTOM_RIGHT,
        SCALED_FROM_TOP_RIGHT,
        SCALED_FROM_TOP_LEFT
    }
    // For setting the tip position in the constructor
    public enum TIP_POS_TYPE {
        ABSOLUTE,
        TOP_RIGHT,
        TOP_CENTERED_UNDER_HB
    }

    // Local format
    protected HB_POS_TYPE hb_type = HB_POS_TYPE.ABSOLUTE;
    protected Hitbox hb;
    private float hb_moveX = 0.0f;
    private float hb_moveY = 0.0f;

    protected TIP_POS_TYPE tip_type = TIP_POS_TYPE.TOP_RIGHT;
    private float tip_moveX = 0.0f;
    private float tip_moveY = 0.0f;

    protected float PRIMARY_TIP_X = 0.0f;
    protected float PRIMARY_TIP_Y = 0.0f;

    // Local tip
    protected boolean usePrimaryTip = true;
    protected PowerTip primary_tip;
    protected ArrayList<PowerTip> tips;

    // Slay the relics integration format
    public static ArrayList<Hitbox> slayTheRelicsHitboxes = new ArrayList<>();
    public static ArrayList<ArrayList<PowerTip>> slayTheRelicsPowerTips = new ArrayList<>();


    /// Params:
    /// w : width of hitbox (nonscaled)
    /// h : height of hitbox (nonscaled)
    /// hbx : hitbox x position (will be moved according to HB_POS_TYPE)
    /// hby : hitbox y position (will be moved according to HB_POS_TYPE)
    /// hb_type : how the hitbox x,y will move (for scaling, see documentation at top)
    /// tipx : where tip will be rendered x
    /// tipy : where tip will be rendered y
    /// tip_type : how the tipx,tipy will be moved (for scaling, see documentation)
    ///
    /// NOTE: may provide cleaner constructors in the future; or allow derived classes to ignore this boilerplate
    /// TODO: probably should scale w/h of hitbox like the others [oops!]
    public CustomHitboxTipItem(float w, float h, float hbx, float hby, HB_POS_TYPE hb_type,
                               float tipx, float tipy, TIP_POS_TYPE tip_type, String header, String body) {
        BaseMod.subscribe(this);

        this.hb = new Hitbox(w, h);

        this.hb_type = hb_type;
        this.hb_moveX = hbx;
        this.hb_moveY = hby;

        this.primary_tip = new PowerTip(header, body);

        this.tip_type = tip_type;
        this.tip_moveX = tipx;
        this.tip_moveY = tipy;

        tips = new ArrayList<>();
    }

    // Can move scaled hitbox locations here
    // (Settings var has 0 for width, height, etc. before this post init is called) -- helps for scaled versions
    @Override
    public void receivePostInitialize() {
        // Setup the hitbox position based on its type
        if (hb_type == HB_POS_TYPE.ABSOLUTE) {
            hb.move(hb_moveX, hb_moveY);
        }
        else {
            float w = (float) Settings.WIDTH;
            float h = (float) Settings.HEIGHT;
            float s = Settings.scale;

            if (hb_type == HB_POS_TYPE.SCALED_FROM_BOTTOM_LEFT)
                hb.move(hb_moveX * s, hb_moveY * s);
            else if (hb_type == HB_POS_TYPE.SCALED_FROM_BOTTOM_RIGHT)
                hb.move(w - hb_moveX * s, hb_moveY * s);
            else if (hb_type == HB_POS_TYPE.SCALED_FROM_TOP_RIGHT)
                hb.move(w - hb_moveX * s, h - hb_moveY * s);
            else if (hb_type == HB_POS_TYPE.SCALED_FROM_TOP_LEFT)
                hb.move(hb_moveX * s, h - hb_moveY * s);
        }

        // Setup the tip position based on its type
        if (tip_type == TIP_POS_TYPE.ABSOLUTE) {
            PRIMARY_TIP_X = tip_moveX;
            PRIMARY_TIP_Y = tip_moveY;
        }
        else {
            float h = (float) Settings.HEIGHT;
            float s = Settings.scale;

            if (tip_type == TIP_POS_TYPE.TOP_RIGHT) {
                PRIMARY_TIP_X = 1550.0F * Settings.scale;
                PRIMARY_TIP_Y = h - 120.0F * s;
            }
            else if (tip_type == TIP_POS_TYPE.TOP_CENTERED_UNDER_HB) {
                PRIMARY_TIP_X = 1550.0F * Settings.scale;
                PRIMARY_TIP_Y = h - 120.0F * s;
            }
        }
    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
        // copied over from old code: unsure if this is actually needed
        hb.encapsulatedUpdate(this);

        if (usePrimaryTip && AbstractDungeon.isPlayerInDungeon() && hb.hovered) {
            tips.clear();
            tips.add(primary_tip);

            // Local rendering
            TipHelper.queuePowerTips(PRIMARY_TIP_X, PRIMARY_TIP_Y, tips);
            //TipHelper.renderGenericTip(TIP_X, TIP_Y, "Current Deck", tip_body);

            // TODO look over this more
        }
    }

    // Can override these with actual stuff, but they aren't needed (may be useful in derived classes
    @Override
    public void hoverStarted(Hitbox hitbox) { }

    @Override
    public void startClicking(Hitbox hitbox) { }

    @Override
    public void clicked(Hitbox hitbox) { }

    ////////////////////////////////////////////////////////////////////////////////

    protected void slayTheRelicsClear() {
        slayTheRelicsHitboxes.clear();
        slayTheRelicsPowerTips.clear();
    }

    protected void setSlayTheRelicsAdd() {
        slayTheRelicsHitboxes.add(hb);
        slayTheRelicsPowerTips.add(tips);
    }

    protected void slayTheRelicsUpdate() {
        slayTheRelicsClear();
        setSlayTheRelicsAdd();
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Setters
    public void setPrimaryTipHeader(String h) {
        primary_tip.header = h;
        slayTheRelicsUpdate();
    }
    public void setPrimaryTipBody(String b) {
        primary_tip.body = b;
        slayTheRelicsUpdate();
    }
    public void setPrimaryTipBoth(String header, String body) {
        primary_tip.header = header;
        primary_tip.body = body;
        slayTheRelicsUpdate();
    }
}
