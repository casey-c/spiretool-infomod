package InfoMod;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import java.util.ArrayList;
import java.util.Vector;

// TODO: lots of refactoring / documentation improvements

public class InfoPanelItem extends TopPanelItem {
    private static final Texture IMG = new Texture("images/icon.png");
    public static final String ID = "ojb_infomod:InfoIcon";

    private ArrayList<PowerTip> tips;
    private PowerTip event_tip, card_tip, help_tip;

    // TODO: remove / clean up
    private CardPlays cardPlays;

    public InfoPanelItem() {
        super(IMG, ID);

        hb_w = 216.0f;

        tips = new ArrayList<>();
        event_tip = new PowerTip("Event Chance", "TODO");
        card_tip = new PowerTip("Card Chances", "TODO");
        help_tip = new PowerTip("Info", "NOTE: Event list does not contain special one time events. (e.g. Ominous Forge, The Joust, etc.)");

        tips.add(event_tip);
        tips.add(card_tip);
        tips.add(help_tip);

        cardPlays = new CardPlays();
    }

    public void setEventsAndShrines(ArrayList<String> eventList, ArrayList<String> shrineList, ArrayList<String> specialList, float[] prAfter, float prEvent, float prMonster, float prShop, float prTreasure) {
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

        // TODO: cleanup
        sb.append(" NL NL Chance to see specific: NL ");
        sb.append(String.format("%.02f%% NL ", prAfter[0] * 100.0f));
        sb.append(" NL After 2 floors: NL ");
        sb.append(String.format("%.02f%%", prAfter[1] * 100.0f));

//        // TMP TODO: remove after testing
        // TODO: add proper one time events to the shrine list!
//        sb.append(" NL NL SPECIAL ONE TIME EVENTS NL ");
//        int index = 0;
//        for (String s : specialList) {
//            sb.append(s);
//
//            if (index < specialList.size() - 1)
//                sb.append(", ");
//            ++index;
//        }

        event_tip.body = sb.toString();

        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_QBOX) == false)
            return;

        SlayTheRelicsIntegration.update("infoPanelItem", hitbox, tips);
    }

    public void setProbabilities(double rare, double rareElite, double unc, double uncElite, int numCards, int numCardsElite) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Next %d cards (%d for elite) NL NL ", numCards, numCardsElite) );
        sb.append(String.format("#yRare: NL #y%.02f%% #y(%.02f%%) NL NL ", rare * 100.0, rareElite * 100.0));
        sb.append(String.format("#bUncommon: NL #b%.02f%% #b(%.02f%%)", unc * 100.0, uncElite * 100.0));

        card_tip.body = sb.toString();

        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_QBOX) == false)
            return;

        SlayTheRelicsIntegration.update("infoPanelItem", hitbox, tips);
    }


    @Override
    protected void onClick() {
        // TODO: might need to quit early if config setting is ignored by this function call
        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_QBOX) == false)
            return;

        //AbstractDungeon.effectList.add(new CardPoofEffect(500, 500));

        // TODO: uncomment this again (WIP)
        cardPlays.toggleVisibility();

        // TODO: remove this cheat (for debugging)
        //AbstractDungeon.getCurrRoom().endBattle();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_QBOX) == false)
            return;

        super.render(sb);

    }


    boolean alreadyHovered = false;

    @Override
    protected void onHover() {
        // TODO: after super call?
        if (ConfigHelper.getInstance().getBool(ConfigHelper.BooleanSettings.SHOW_QBOX) == false)
            return;

        super.onHover();

        float TIP_Y = (float)Settings.HEIGHT - 120.0F * Settings.scale;
        float TOP_RIGHT_TIP_X = (float) InputHelper.mX * Settings.scale;

        // Add to render queue
        TipHelper.queuePowerTips(TOP_RIGHT_TIP_X, TIP_Y, tips);

        // Play sound if first time hovered
        if (!alreadyHovered) {
            SoundHelper.playUIHoverSound();
            alreadyHovered = true;
        }
    }

    @Override
    protected void onUnhover() {
        super.onUnhover();
        alreadyHovered = false;
    }
}
