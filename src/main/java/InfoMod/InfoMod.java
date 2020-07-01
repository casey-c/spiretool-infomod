package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostDeathSubscriber;
import basemod.interfaces.PostDungeonUpdateSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@SpireInitializer
public class InfoMod implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonUpdateSubscriber, PostDeathSubscriber {;

    private static int curr_potion_chance = -1;
    private static int curr_rare_chance = -1;
    private static int curr_floor = -1;

    private static String curr_boss = "";

    private static PotionPanelItem potionPanelItem;
    private static InfoPanelItem infoPanelItem;

    private static CustomHitboxTipItem deckTipItem;
    private static CustomHitboxTipItem bossTipItem;

    private static int cards_hash = 0;
    private static int upgrade_cards_hash = 0;

    public InfoMod() {
        BaseMod.subscribe(this);

        //cards = new ArrayList<>();
    }

    public static void initialize() {
        new InfoMod();

        // does some crazy shit
        // also useful for hitboxes
        //Settings.isDebug = true;

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

        float cx = (float)Settings.WIDTH - 117.0f * Settings.scale;
        float cy = (float)Settings.HEIGHT - 33.0f * Settings.scale;

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
    // right now searing blow won't update asap. oh well
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


    @Override
    public void receivePostDungeonUpdate() {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room != null) {
            boolean anyChanges = false;

            // Potions
            int new_potion_chance = room.blizzardPotionMod + 40;

            // Relic overrides
            AbstractPlayer player = AbstractDungeon.player;
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

            // Rare chances
            int new_rare_chance = AbstractDungeon.cardBlizzRandomizer;
            if (curr_rare_chance != new_rare_chance) {
                anyChanges = true;
                updateCardChances(new_rare_chance);
            }

            // Event chances
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

                // elite (useless), monster, shop, treasure
                float prMonster = eventChanceVec.get(1);
                float prShop = eventChanceVec.get(2);
                float prTreasure = eventChanceVec.get(3);
                float prEvent = 1.0f - prMonster - prShop - prTreasure;

                // Update UI
                infoPanelItem.setEventsAndShrines(eventList, shrineList, prEvent, prMonster, prShop, prTreasure);

//                for (String y : eventList) { System.out.println("events: " + y); }
//                for (String y : shrineList) { System.out.println("shrines: " + y); }
            }

            // Cards
            if (updateCards())
                anyChanges = true;

            // Boss
            if (AbstractDungeon.bossList.size() > 0 && AbstractDungeon.bossList.get(0) != curr_boss) {
                curr_boss = AbstractDungeon.bossList.get(0);
                System.out.println("OJB: boss update: " + curr_boss);
                bossTipItem.setPrimaryTipBody(curr_boss);
                anyChanges = true;
            }

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
    }

    @Override
    public void receivePostDeath() {
        // TODO: put this in a better spot? might want to check the deck at the end maybe?
        //SlayTheRelicsIntegration.reset();
        //SlayTheRelicsIntegration.print();
    }
}
