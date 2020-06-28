package InfoMod;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class InfoPanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture("images/icon.png");
    public static final String ID = "ojb_infomod:InfoIcon";

    private int potion_chance = 40;

    private ArrayList<PowerTip> tips;
    private PowerTip event_tip, card_tip, help_tip;

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
        sb.append(eventList.size());
        sb.append(String.format(" Available events (%.02f%%): NL NL ", ((prEvent * 0.75f) * 100.0f)));

        Vector<String> eventVec = new Vector<>(eventList);
        for (int i = 0; i < eventVec.size(); ++i) {
            sb.append(eventVec.elementAt(i));
            if (i < eventVec.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" NL NL ");

        // Shrines
        sb.append(shrineList.size());
        sb.append(String.format(" Available shrines (%.02f%%): NL NL ", ((prEvent * 0.25f) * 100.0f)));

        Vector<String> shrineVec = new Vector<>(shrineList);
        for (int i = 0; i < shrineVec.size(); ++i) {
            sb.append(shrineVec.elementAt(i));
            if (i < shrineVec.size() - 1) {
                sb.append(", ");
            }
        }

        // Extra
        sb.append(String.format(" NL NL Monster (%.02f%%) NL ", prMonster * 100.0f));
        sb.append(String.format("Shop (%.02f%%) NL ", prShop * 100.0f));
        sb.append(String.format("Treasure (%.02f%%) NL ", prTreasure * 100.0f));

        event_tip.body = sb.toString();
    }

    public void setProbabilities(double rare, double rareElite, double unc, double uncElite, int numCards, int numCardsElite) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Next %d cards (%d for elite) NL NL ", numCards, numCardsElite) );
        sb.append(String.format("Rare: NL %.02f%% (%.02f%%) NL NL ", rare * 100.0, rareElite * 100.0));
        sb.append(String.format("Uncommon: NL %.02f%% (%.02f%%)", unc * 100.0, uncElite * 100.0));

//        sb.append("Rare Cards: NL ");
//        sb.append(String.format("Regular: %.02f NL ", rare));
//        sb.append(String.format("Elite: %.02f NL ", rareElite));
//
//        sb.append("NL Uncommon Cards: NL ");
//        sb.append(String.format("Regular: %.02f NL ", unc));
//        sb.append(String.format("Elite: %.02f NL ", uncElite));

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
        // TODO: move with mouse
        //float TOP_RIGHT_TIP_X = (float) Settings.WIDTH - 683.0f * Settings.scale;
        float TOP_RIGHT_TIP_X = (float) InputHelper.mX * Settings.scale;

        // Add to render queue
        TipHelper.queuePowerTips(TOP_RIGHT_TIP_X, TIP_Y, tips);

        // Debug (fires only on change to avoid spamming logger)
        if (!isHovered) {
            isHovered = true;
            System.out.println("OJB: hovered");
            System.out.println("tip y = " + TIP_Y);
        }
    }

    @Override
    protected void onUnhover() {
        super.onUnhover();

        if (isHovered) {
            isHovered = false;
            System.out.println("OJB: unhovered");
        }
    }
}
