package InfoMod;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.gikk.twirk.SETTINGS;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.vfx.GenericSmokeEffect;
import com.megacrit.cardcrawl.vfx.PetalEffect;
import com.megacrit.cardcrawl.vfx.SpotlightEffect;
import com.megacrit.cardcrawl.vfx.combat.BlizzardEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;
import com.megacrit.cardcrawl.vfx.combat.GrandFinalEffect;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

@SpireInitializer
public class InfoPanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture("images/icon.png");
    public static final String ID = "ojb_infomod:InfoIcon";

    private int potion_chance = 40;


    private ArrayList<PowerTip> tips;
    private PowerTip event_tip, card_tip, help_tip;

    // SlayTheRelics integration
    public static ArrayList<Hitbox> slayTheRelicsHitboxes = new ArrayList<>();
    public static ArrayList<ArrayList<PowerTip>> slayTheRelicsPowerTips = new ArrayList<>();

    boolean isHovered = false;

    public InfoPanelItem() {
        super(IMG, ID);

        //this.hitbox.width = 221.0f;
        //this.hitbox.move(this.hitbox.cX -158.0f, this.hitbox.cY);
        //setX(this.hitbox.cX - 158.0f);
        //setX(700.0f);
        //hb_w = 100.0f;

        hb_w = 216.0f;


        tips = new ArrayList<>();
        event_tip = new PowerTip("Event Chance", "TODO");
        card_tip = new PowerTip("Card Chances", "TODO");
        help_tip = new PowerTip("Info", "NOTE: Event list does not contain special one time events. (e.g. Ominous Forge, The Joust, etc.)");

        tips.add(event_tip);
        tips.add(card_tip);
        tips.add(help_tip);
    }

    public void setEventsAndShrines(ArrayList<String> eventList, ArrayList<String> shrineList, float prEvent, float prMonster, float prShop, float prTreasure) {
        StringBuilder sb = new StringBuilder();

        // Events
        sb.append("#g");
        sb.append(eventList.size());
        sb.append(String.format(" #gAvailable #gevents (%.02f%%): NL NL ", ((prEvent * 0.75f) * 100.0f)));

        Vector<String> eventVec = new Vector<>(eventList);
        for (int i = 0; i < eventVec.size(); ++i) {
            sb.append(eventVec.elementAt(i));
            if (i < eventVec.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" NL NL ");

        // Shrines
        sb.append("#g");
        sb.append(shrineList.size());
        sb.append(String.format(" #gAvailable #gshrines (%.02f%%): NL NL ", ((prEvent * 0.25f) * 100.0f)));

        Vector<String> shrineVec = new Vector<>(shrineList);
        for (int i = 0; i < shrineVec.size(); ++i) {
            sb.append(shrineVec.elementAt(i));
            if (i < shrineVec.size() - 1) {
                sb.append(", ");
            }
        }

        // Extra
        sb.append(String.format(" NL NL #gMonster (%.02f%%) NL ", prMonster * 100.0f));
        sb.append(String.format("#gShop (%.02f%%) NL ", prShop * 100.0f));
        sb.append(String.format("#gTreasure (%.02f%%) NL ", prTreasure * 100.0f));

        event_tip.body = sb.toString();
    }

    public void setProbabilities(double rare, double rareElite, double unc, double uncElite, int numCards, int numCardsElite) {
        StringBuilder sb = new StringBuilder();

        // TODO: figure out what #e #r etc. do
        // NOTE: [E] is the energy symbol
        sb.append(String.format("Next %d cards (%d for elite) NL NL ", numCards, numCardsElite) );
        sb.append(String.format("#yRare: NL #y%.02f%% #y(%.02f%%) NL NL ", rare * 100.0, rareElite * 100.0));
        sb.append(String.format("#bUncommon: NL #b%.02f%% #b(%.02f%%)", unc * 100.0, uncElite * 100.0));

        card_tip.body = sb.toString();
    }


    @Override
    protected void onClick() {
        System.out.println("OJB: info panel item clicked!");

        AbstractDungeon.effectList.add(new CardPoofEffect(500, 500));

    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

    }

    @Override
    protected void onHover() {
        super.onHover();

        float TIP_Y = (float)Settings.HEIGHT - 120.0F * Settings.scale;
        //float TOP_RIGHT_TIP_X = 1550.0F * Settings.scale;
        //float TOP_RIGHT_TIP_X = (float) Settings.WIDTH - 683.0f * Settings.scale;
        float TOP_RIGHT_TIP_X = (float) InputHelper.mX * Settings.scale;

        // Add to render queue
        TipHelper.queuePowerTips(TOP_RIGHT_TIP_X, TIP_Y, tips);
    }
}
