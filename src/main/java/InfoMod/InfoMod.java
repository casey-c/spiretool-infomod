package InfoMod;

import basemod.BaseMod;
import basemod.abstracts.CustomSavableRaw;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: lots of refactoring and cleanup
// this mod is still very new so it's been low priority to make the code nice

@SpireInitializer
public class InfoMod implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonUpdateSubscriber, PostDeathSubscriber, PostDungeonInitializeSubscriber, CustomSavableRaw {;

    private static int curr_potion_chance = -1;
    private static int curr_rare_chance = -1;
    private static int curr_floor = -1;

    //private static String curr_boss = "";

//    private static String boss_act1 = "";
//    private static String boss_act2 = "";
//    private static String boss_act3_1 = "";
//    private static String boss_act3_2 = "";
//    private static String boss_act4 = "";

    private BossStringsSaveable bossStringsSaveable = new BossStringsSaveable();

    private static PotionPanelItem potionPanelItem;
    private static InfoPanelItem infoPanelItem;

    private static CustomHitboxTipItem deckTipItem;
    private static CustomHitboxTipItem bossTipItem;
    //private static CustomHitboxTipItem goldTipItem; // TODO


    private static int cards_hash = 0;
    private static int upgrade_cards_hash = 0;

    public InfoMod() {
        BaseMod.subscribe(this);

        //cards = new ArrayList<>();
        BaseMod.addSaveField("ojb_bosses", this);
    }

    public static void initialize() {
        new InfoMod();

        deckTipItem = new CustomHitboxTipItem(
                "deckTipItem",
                67,
                67,
                117.0f,
                33.0f,
                CustomHitboxTipItem.HB_POS_TYPE.SCALED_FROM_TOP_RIGHT,
                0.0f,
                0.0f,
                CustomHitboxTipItem.TIP_POS_TYPE.TOP_RIGHT,
                "Deck",
                "..."
        );

        bossTipItem = new CustomHitboxTipItem(
                "bossTipItem",
                67,
                67,
                190.0f,
                33.0f,
                CustomHitboxTipItem.HB_POS_TYPE.SCALED_FROM_TOP_RIGHT,
                0.0f,
                0.0f,
                CustomHitboxTipItem.TIP_POS_TYPE.TOP_RIGHT,
                "Current Boss",
                "..."
        );




        //float cx = (float)Settings.WIDTH - 117.0f * Settings.scale;
        //float cy = (float)Settings.HEIGHT - 33.0f * Settings.scale;

    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        // TODO maybe

//        System.out.println("OJB: post battle");
//        System.out.println("baseRareCardChance:" + abstractRoom.baseRareCardChance);
//        System.out.println("rareCardChance:" + abstractRoom.rareCardChance);
//        System.out.println("isBattleOver:" + abstractRoom.isBattleOver);
//        System.out.println("room.blizzardPotionMod:" + abstractRoom.blizzardPotionMod);
//        System.out.println("EventHelper.getChances()" + EventHelper.getChances());
//        System.out.println("------------------------");
    }

    private void updateCardChances(int new_rare_chance) {
        curr_rare_chance = new_rare_chance;
        System.out.println("OJB UPDATE: Rare CBR = " + (curr_rare_chance));

        int numCards = 3;
        int numCardsElite = 3;

        int rareChance = 3;
        int rareChanceElite = 10;

        // Relic overrides
        AbstractPlayer player = AbstractDungeon.player;
        if (player != null) {
            if (player.hasRelic("Question Card")) {
                numCards += 1;
                numCardsElite += 1;
            }
            if (player.hasRelic("Busted Crown")) {
                numCards -= 2;
                numCardsElite -= 2;
            }
            if (player.hasRelic("Prayer Wheel")) {
                numCards *= 2;
            }

            if (player.hasRelic("Nloth's Gift")) {
                rareChance *= 3;
                rareChanceElite *= 3;
            }
        }

        // Compute probabilities
        double prRare = ProbabiltyUtils.computeCardProb(curr_rare_chance, numCards, rareChance, ProbabiltyUtils.CARD_RARITY.CARD_RARE);
        double prRareElite = ProbabiltyUtils.computeCardProb(curr_rare_chance, numCardsElite, rareChanceElite, ProbabiltyUtils.CARD_RARITY.CARD_RARE);

        double prUnc = ProbabiltyUtils.computeCardProb(curr_rare_chance, numCards, rareChance, ProbabiltyUtils.CARD_RARITY.CARD_UNCOMMON);
        double prUncElite = ProbabiltyUtils.computeCardProb(curr_rare_chance, numCardsElite, rareChanceElite, ProbabiltyUtils.CARD_RARITY.CARD_UNCOMMON);

        infoPanelItem.setProbabilities(prRare, prRareElite, prUnc, prUncElite, numCards, numCardsElite);

    }

    // TODO: make this more efficient / no need to run every frame.
    //     figure out a way to subscribe to a card upgrade / removal / transform event (e.g. from events / campfires)
    //     will probably need to spire patch unfortunately :(
    //     NOTE: right now searing blow won't update properly
    private boolean updateCards() {
        int new_cards_hash = AbstractDungeon.player.masterDeck.getCardNames().hashCode();
        int new_upgrade_cards_hash = AbstractDungeon.player.masterDeck.getUpgradableCards().getCardNames().hashCode();

        // TODO: maybe add special logic for searing blow, but probably nah (don't want to make this check expensive)
        // boolean hasSearingBlow = CardHelper.hasCardWithID("Searing Blow");

        // TODO: make this not super awful and inefficient
        if (cards_hash != new_cards_hash || upgrade_cards_hash != new_upgrade_cards_hash) {
            ArrayList<CardSave> cards = AbstractDungeon.player.masterDeck.getCardDeck();

            HashMap<String, Integer> cleanedCardStrings = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (CardSave card : cards) {
                sb.setLength(0);

                CardStrings s = CardCrawlGame.languagePack.getCardStrings(card.id);
                sb.append(s.NAME);

                if (card.upgrades > 0)
                    sb.append("+");
                if (card.upgrades > 1)
                    sb.append(card.upgrades);

                // More efficient ways exist, but we want unique keys and the count of each
                // E.g. don't want Strike, Strike, Strike, Strike+ but:
                //   [3] Strike, Strike+
                String key = sb.toString();
                int count = cleanedCardStrings.containsKey(key) ? cleanedCardStrings.get(key) : 0;
                cleanedCardStrings.put(key, count + 1);
            }

            // Sort by card name
            ArrayList<Pair<String, Integer>> finalCards = new ArrayList<>();
            for (Map.Entry<String, Integer> pair : cleanedCardStrings.entrySet())
                finalCards.add(Pair.of(pair.getKey(), pair.getValue()));
            finalCards.sort(Comparator.comparing(Pair::getLeft));

            // To a single string
            sb.setLength(0);
            int index = 0;

            for (Pair<String, Integer> pair : finalCards) {
                String key = pair.getLeft();
                int count = pair.getRight();

                // e.g. [4] Strike
                if (count > 1) {
                    sb.append("[");
                    sb.append(count);
                    sb.append("] ");
                }
                sb.append(key);

                if (index < cleanedCardStrings.size() - 1)
                    sb.append(" NL ");

                ++index;
            }

//            deckDropdownItem.setString(sb.toString());
            deckTipItem.setPrimaryTipBody(sb.toString());

            cards_hash = new_cards_hash;
            upgrade_cards_hash = new_upgrade_cards_hash;
            return true;
        }

        return false;
    }

    // Boss
    // TODO: more testing to make sure it's ok. I think it might be working finally after hours of retries.
    //   This final version uses the BaseMod saving mechanism to make sure the info is loaded with the game correctly
    //   It also makes sure we don't cheat by seeing future bosses in advance, by adding a couple of annoying hardcoded
    //   edge cases to only update this string at the proper times
    // NOTE: this still updates every tick, not when the room is changed, so we can have it update on boss chest
    //   floors and let you see the text on the map starting page
    private void updateBoss() {
        ArrayList<String> bossList = AbstractDungeon.bossList;

        if ((curr_floor == 0) && (bossList.size() > 1)) {
            bossStringsSaveable.act1 = bossList.get(0);

            bossStringsSaveable.combined = RenderingUtils.colorify("#g", bossStringsSaveable.act1);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        } else if ((curr_floor == 17) && (bossList.size() == 3)) { // updates after hitting proceed
            bossStringsSaveable.act2 = bossList.get(0);
            //bossTipItem.setPrimaryTipBody(bossStringsSaveable.act1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act2));

            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act2);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        } else if ((curr_floor == 34) && (bossList.size() == 3)) {
            bossStringsSaveable.act3_1 = bossList.get(0);
            bossStringsSaveable.act3_2 = bossList.get(1);
            //bossTipItem.setPrimaryTipBody(bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_1));

            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_1);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        }
        else if (curr_floor == 51) {
            //bossTipItem.setPrimaryTipBody(bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " +  bossStringsSaveable.act3_1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_2));
            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " +  bossStringsSaveable.act3_1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_2);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        }
        else if (curr_floor == 52) {
            //bossTipItem.setPrimaryTipBody(bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " +  bossStringsSaveable.act3_1 + " NL " + bossStringsSaveable.act3_2 + " NL " + "#gThe #gHeart");
            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " +  bossStringsSaveable.act3_1 + " NL " + bossStringsSaveable.act3_2 + " NL " + "#gThe #gHeart";
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        }
    }

    private boolean updateEvents(@Nullable AbstractPlayer player) {
        boolean anyChanges = false;

        int new_curr_floor = AbstractDungeon.floorNum;
        if (curr_floor != new_curr_floor) {
            curr_floor = new_curr_floor;

            anyChanges = true;

            ArrayList<String> eventList = new ArrayList<String>(AbstractDungeon.eventList);
            //eventList.addAll(AbstractDungeon.specialOneTimeEventList);
            ArrayList<String> shrineList = new ArrayList<String>(AbstractDungeon.shrineList);

            // Sort to avoid cheating
            eventList.sort(String::compareTo);
            shrineList.sort(String::compareTo);

            // Compute probabilities
            System.out.println("Loading post combat?: " + AbstractDungeon.loading_post_combat);
            ArrayList<Float> eventChanceList = AbstractDungeon.loading_post_combat ? EventHelper.getChancesPreRoll() : EventHelper.getChances();
            Vector<Float> eventChanceVec = new Vector<>(eventChanceList);

            // eventChanceVec: elite (useless), monster, shop, treasure
            float prMonster = eventChanceVec.get(1);
            float prShop = eventChanceVec.get(2);
            float prTreasure = eventChanceVec.get(3);
            float prEvent = 1.0f - prMonster - prShop - prTreasure;

            // JUZU BRACELET fix
            if (player != null && player.hasRelic("Juzu Bracelet")) {
                prEvent += prMonster;
                prMonster = 0.0f;
            }

            int totalEventsInPool = eventList.size();
            float[] pr = ProbabiltyUtils.chanceOfSeeingEventAfter2(prMonster, prTreasure, prShop, totalEventsInPool);

            System.out.println("OJB: total events in pool: " + totalEventsInPool);
            System.out.println("OJB: probability after 1: " + pr[0] + ", pr after 2: " + pr[1]);

            // Update UI
            infoPanelItem.setEventsAndShrines(eventList, shrineList, AbstractDungeon.specialOneTimeEventList, pr, prEvent, prMonster, prShop, prTreasure);

        }

        return anyChanges;

    }

    private boolean updatePotions(AbstractRoom room, AbstractPlayer player) {
        boolean anyChanges = false;

        // Potions
        int new_potion_chance = room.blizzardPotionMod + 40;

        // Relic overrides
        if (player != null) {
            if (player.hasRelic("White Beast Statue")) {
                new_potion_chance = 100;
            }
            if (player.hasRelic("Sozu")) {
                new_potion_chance = 0;
            }
        }

        // Update UI
        if (curr_potion_chance != new_potion_chance) {
            anyChanges = true;
            curr_potion_chance = new_potion_chance;

            System.out.println("OJB UPDATE: Potion Chance = " + curr_potion_chance);
            potionPanelItem.setPotionChance(curr_potion_chance);
        }

        return anyChanges;
    }


    @Override
    public void receivePostDungeonUpdate() {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room != null) {
            boolean anyChanges = false;

            // Rare chances
            int new_rare_chance = AbstractDungeon.cardBlizzRandomizer;
            if (curr_rare_chance != new_rare_chance) {
                anyChanges = true;
                updateCardChances(new_rare_chance);
            }

            // Event chances
            AbstractPlayer player = AbstractDungeon.player;

            anyChanges |= updatePotions(room, player);
            anyChanges |= updateEvents(player);
            anyChanges |= updateCards();
            updateBoss();

            // Logging
            if (anyChanges) {
                System.out.println("OJB: update found changes");
                System.out.println("--------------------------------------");

                SlayTheRelicsIntegration.print();

                System.out.println("OJB: str integration found: " +SlayTheRelicsIntegration.slayTheRelicsHitboxes.size() + " " + SlayTheRelicsIntegration.slayTheRelicsPowerTips.size());
            }
        }
    }

    @Override
    public void receivePostInitialize() {
        infoPanelItem = new InfoPanelItem();
        potionPanelItem = new PotionPanelItem();

        BaseMod.addTopPanelItem(infoPanelItem);
        BaseMod.addTopPanelItem(potionPanelItem);

        // After loading in the saved tip, make sure to set it in our tip renderer class
        //bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
    }

    @Override
    public void receivePostDeath() {
        // TODO: put this in a better spot? might want to check the deck at the end maybe?
        //SlayTheRelicsIntegration.reset();
        //SlayTheRelicsIntegration.print();
    }

    @Override
    public void receivePostDungeonInitialize() {
        // TODO: (this won't work nicely yet, but it might be possible with this direction)
        // the problem with the regular approach is that the goldHb moves depending on the character (as different
        // characters' names have different lengths; it's not fixed like the right hand side ones we used prior)
        // alternative approach is to use a patch, though i couldn't figure out the replace method
        // the idea here is to use the TopPanel.goldHb object itself (which is nice and public), but this isn't the
        // subscriber event to have it fire correctly unfortunately. (other init events have a null AbstractDungeon,
        // we might need to put it into a startRun() sort of event or load event or something idk.) -- this at least will
        // print out the stuff correctly as AbstractDungeon isn't null, but it still has issues on reloads.

//        Hitbox goldHb = AbstractDungeon.topPanel.goldHb;
//        System.out.println("*******************************************************************");
//        System.out.println("OJB: gold hitbox cX" + goldHb.cX);
//        System.out.println("OJB: gold hitbox cY" + goldHb.cY);
//        System.out.println("OJB: gold hitbox x" + goldHb.x);
//        System.out.println("OJB: gold hitbox y" + goldHb.y);
//        System.out.println("*******************************************************************");
//
//        //goldHb.width
//
//        goldTipItem = new CustomHitboxTipItem(
//                "goldTipItem",
//                goldHb.width,
//                goldHb.height,
//                goldHb.x,
//                goldHb.y,
//                CustomHitboxTipItem.HB_POS_TYPE.ABSOLUTE,
//                0.0f,
//                0.0f,
//                CustomHitboxTipItem.TIP_POS_TYPE.TOP_CENTERED_UNDER_HB,
//                "Gold",
//                "TODO WIP"
//        );
    }

    @Override
    public JsonElement onSaveRaw() {
        JsonArray elt = new JsonArray();
        elt.add(bossStringsSaveable.act1);
        elt.add(bossStringsSaveable.act2);
        elt.add(bossStringsSaveable.act3_1);
        elt.add(bossStringsSaveable.act3_2);
        elt.add(bossStringsSaveable.combined);
        return elt;
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            JsonArray arr = jsonElement.getAsJsonArray();

            // probably don't need to do all this checking lol
            if (arr.size() == 5) {
                if (arr.get(0).isJsonPrimitive())
                    bossStringsSaveable.act1 = arr.get(0).getAsString();
                if (arr.get(1).isJsonPrimitive())
                    bossStringsSaveable.act2 = arr.get(1).getAsString();
                if (arr.get(2).isJsonPrimitive())
                    bossStringsSaveable.act3_1 = arr.get(2).getAsString();
                if (arr.get(3).isJsonPrimitive())
                    bossStringsSaveable.act3_2 = arr.get(3).getAsString();
                if (arr.get(4).isJsonPrimitive())
                    bossStringsSaveable.combined = arr.get(4).getAsString();

                // Make sure to set the actual thing
                bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
            }
        }
    }
}
