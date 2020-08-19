package InfoMod;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.abstracts.CustomSavableRaw;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: lots of refactoring and cleanup
//   potion tracking and event tracking will eventually have their own self contained classes

@SpireInitializer
public class InfoMod implements PostInitializeSubscriber, PostDungeonUpdateSubscriber, PostDungeonInitializeSubscriber, CustomSavableRaw {;

    private static int curr_potion_chance = -1;
    private static int curr_rare_chance = -1;
    private static int curr_floor = -1;

    //private BossStringsSaveable bossStringsSaveable = new BossStringsSaveable();

    private static PotionPanelItem potionPanelItem;
    private static InfoPanelItem infoPanelItem;

    private static CustomHitboxTipItem deckTipItem;
    private static CustomHitboxTipItem bossTipItem;

    private static int cards_hash = 0;
    private static int upgrade_cards_hash = 0;

    public InfoMod() {
        BaseMod.subscribe(this);
        BaseMod.addSaveField("OJB_INFOMOD", this);
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

        //BossStringsSaveable bossStringsSaveable = BossStringsSaveable.get();
        BossStringsSaveable bossStringsSaveable = SaveableManager.bosses;

        if ((curr_floor == 0) && (bossList.size() > 1)) {
            bossStringsSaveable.act1 = bossList.get(0);

            bossStringsSaveable.combined = RenderingUtils.colorify("#g", bossStringsSaveable.act1);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        } else if ((curr_floor == 17) && (bossList.size() == 3)) { // updates after hitting proceed
            bossStringsSaveable.act2 = bossList.get(0);
            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act2);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        } else if ((curr_floor == 34) && (bossList.size() == 3)) {
            bossStringsSaveable.act3_1 = bossList.get(0);
            bossStringsSaveable.act3_2 = bossList.get(1);
            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_1);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        }
        else if (curr_floor == 51) {
            bossStringsSaveable.combined = bossStringsSaveable.act1 + " NL " + bossStringsSaveable.act2 + " NL " +  bossStringsSaveable.act3_1 + " NL " + RenderingUtils.colorify("#g", bossStringsSaveable.act3_2);
            bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
        }
        else if (curr_floor == 52) {
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
            ArrayList<String> shrineList = new ArrayList<String>(AbstractDungeon.shrineList);

            // Sort to avoid cheating
            eventList.sort(String::compareTo);
            shrineList.sort(String::compareTo);

            // Compute probabilities
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

//            System.out.println("OJB: total events in pool: " + totalEventsInPool);
//            System.out.println("OJB: probability after 1: " + pr[0] + ", pr after 2: " + pr[1]);

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

            //System.out.println("OJB UPDATE: Potion Chance = " + curr_potion_chance);
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

            // DEBUG Logging
            if (anyChanges) {
//                System.out.println("OJB: update found changes");
//                System.out.println("--------------------------------------");
//                SlayTheRelicsIntegration.print();
//                System.out.println("OJB: str integration found: " +SlayTheRelicsIntegration.slayTheRelicsHitboxes.size() + " " + SlayTheRelicsIntegration.slayTheRelicsPowerTips.size());
            }
        }
    }

    @Override
    public void receivePostInitialize() {
        infoPanelItem = new InfoPanelItem();
        potionPanelItem = new PotionPanelItem();

        BaseMod.addTopPanelItem(infoPanelItem);
        BaseMod.addTopPanelItem(potionPanelItem);

        // Main Menu -> Mods -> InfoMod -> Config
        ConfigHelper.setupConfigMenu(bossTipItem, deckTipItem);

        // Setup all the user facing Config options (i.e. Main Menu -> Mods -> Info Mod -> Config)
//        ModPanel modPanel = new ModPanel();
//        float titleY = 745.0f * Settings.scale;
//
//        float leftColX = 400.0f * Settings.scale;
//        float rightColX = 1014.0f * Settings.scale;
//
//        float firstDescY = 661.0f * Settings.scale;
//        float itemOffsetY = 144.0f * Settings.scale; // 130.0 height / 14 gap
//
//        modPanel.addUIElement(new InfoModConfigWrappedLabel("Info Mod Config", leftColX, titleY, Settings.CREAM_COLOR, FontHelper.bannerFont, modPanel));
//        //modPanel.addUIElement(new InfoModConfigWrappedLabel("Info mod has a selection of optional modules for customizing the level of detail provided. While in a run, mousing over a particular module will usually provide additional details in the form of a tool tip.", leftColX, titleOverviewY, Settings.CREAM_COLOR, FontHelper.tipBodyFont, modPanel));
//
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                leftColX, firstDescY,
//                "Monster Compendium",
//                "Right click an enemy while in combat to see their AI and moveset. Right click again to close this overlay.",
//                modPanel,
//                ConfigHelper.BooleanSettings.SHOW_MONSTER_DETAILS
//        ));
//
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                leftColX, firstDescY - itemOffsetY,
//                "Potion Chance Tracker",
//                "Displays the chance to see a potion after the next few combats. Shown as text on the top bar.",
//                modPanel,
//                ConfigHelper.BooleanSettings.SHOW_POTIONS
//        ));
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                leftColX, firstDescY - itemOffsetY - itemOffsetY,
//                "Event Chance Tracker",
//                "Displays the possible events you can get in the remaining question mark floors of the act. Shown as a [?] box on the top bar.",
//                modPanel,
//                ConfigHelper.BooleanSettings.SHOW_QBOX
//        ));
//
//        // Second column
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                rightColX, firstDescY,
//                "Map Tool Tip Override (Show Bosses)",
//                "Mousing over the map icon in the top right now shows the bosses you face throughout the run.",
//                modPanel,
//                ConfigHelper.BooleanSettings.SHOW_MAP_TIP,
//                modToggleButton -> {
//                    bossTipItem.enabled = modToggleButton.enabled;
//                }
//        ));
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                rightColX, firstDescY - itemOffsetY,
//                "Deck Tool Tip Override",
//                "Mousing over the deck icon in the top right now shows the contents of your deck in a quick access tool tip.",
//                modPanel,
//                ConfigHelper.BooleanSettings.SHOW_DECK_TIP,
//                modToggleButton -> {
//                    deckTipItem.enabled = modToggleButton.enabled;
//                }
//        ));
//        modPanel.addUIElement(new InfoModConfigDescBool(
//                rightColX, firstDescY - itemOffsetY - itemOffsetY,
//                "Special 80% Potion Chance Effect",
//                "Inspired by twitch.tv/terrenceMHS",
//                modPanel,
//                ConfigHelper.BooleanSettings.TERR80
//        ));
//
//        BaseMod.registerModBadge(new Texture("images/icon_32.png"),
//                "Info Mod",
//                "ojb",
//                "Displays tedious to calculate information",
//                modPanel);
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

    @Override public JsonElement onSaveRaw() { return SaveableManager.save(); }
    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        SaveableManager.load(jsonElement);

        // Set the initial boss tip item to have the proper starter text
        bossTipItem.setPrimaryTipBody(SaveableManager.bosses.combined);
    }
}
