package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostDungeonUpdateSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue.Save;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

@SpireInitializer
public class InfoMod implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonUpdateSubscriber {

    private static int curr_potion_chance = -1;
    private static int curr_rare_chance = -1;
    private static int curr_floor = -1;

    private static PotionPanelItem potionPanelItem;
    private static InfoPanelItem infoPanelItem;

    public InfoMod() {
        BaseMod.subscribe(this);

    }

    public static void initialize() {
        new InfoMod();

        // does some crazy shit
        // also useful for hitboxes
//        Settings.isDebug = true;

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

                for (String y : eventList) {
                    System.out.println("events: " + y);
                }
                for (String y : shrineList) {
                    System.out.println("shrines: " + y);
                }
            }

            // Logging
            if (anyChanges) {

                System.out.println("--------------------------------------");
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
}
