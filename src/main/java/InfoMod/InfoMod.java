package InfoMod;

import basemod.BaseMod;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostDungeonUpdateSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpireInitializer
public class InfoMod implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonUpdateSubscriber {

    private static int curr_potion_chance;
    private static int curr_rare_chance;

    private static InfoPanelItem infoPanelItem;

    public InfoMod() {
        BaseMod.subscribe(this);

    }

    public static void initialize() {
        new InfoMod();

        // does some crazy shit
        // also useful for hitboxes
        //Settings.isDebug = true;

        // Set defaults
        curr_potion_chance = -1;
        curr_rare_chance = -1;
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

    @Override
    public void receivePostDungeonUpdate() {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room != null) {
            boolean anyChanges = false;

            // Potions
            int new_potion_chance = room.blizzardPotionMod;
            if (curr_potion_chance != new_potion_chance) {
                anyChanges = true;
                curr_potion_chance = new_potion_chance;

                System.out.println("OJB UPDATE: Potion Chance = " + (40 + curr_potion_chance));
                infoPanelItem.setPotionChance(40 + curr_potion_chance);
            }

            // Rare chances
            int new_rare_chance = AbstractDungeon.cardBlizzRandomizer;
            if (curr_rare_chance != new_rare_chance) {
                anyChanges = true;
                curr_rare_chance = new_rare_chance;
                System.out.println("OJB UPDATE: Rare CBR = " + (curr_rare_chance));
            }

            // TODO
            if (anyChanges) {
                System.out.println("--------------------------------------");
            }
        }
    }

    @Override
    public void receivePostInitialize() {
        infoPanelItem = new InfoPanelItem();
        BaseMod.addTopPanelItem(infoPanelItem);
    }
}
