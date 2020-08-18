package InfoMod;

import basemod.BaseMod;
import basemod.ModButton;
import basemod.ModPanel;
import basemod.ModTextPanel;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.io.IOException;
import java.util.Properties;

/*
    A wrapper class for SpireConfig. InfoMod specific options can be reached from the ConfigSettings enum
 */
public class ConfigHelper {
    // Singleton pattern
    private static class ConfigHolder {
        private static final ConfigHelper INSTANCE = new ConfigHelper();
    }

    public static ConfigHelper getInstance() {
        return ConfigHolder.INSTANCE;
    }

    // Alternative to lots of static strings -- not sure which is more efficient / better to use.
    public enum BooleanSettings {
        SHOW_QBOX("enableBox"),
        SHOW_MONSTER_DETAILS("enableMonsterDetails"),
        SHOW_POTIONS("enablePotions"),
        SHOW_DECK_TIP("enableDeck"),
        SHOW_MAP_TIP("enableMap"),
        TERR80("enableTerr80");

        private final String val;

        private BooleanSettings(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    private Properties defaults;
    private SpireConfig spireConfig;

    private ConfigHelper() {
        defaults = new Properties();
        defaults.put(ConfigHelper.BooleanSettings.SHOW_QBOX.toString(), true);
        defaults.put(ConfigHelper.BooleanSettings.SHOW_POTIONS.toString(), true);
        defaults.put(ConfigHelper.BooleanSettings.SHOW_DECK_TIP.toString(), true);
        defaults.put(ConfigHelper.BooleanSettings.SHOW_MAP_TIP.toString(), true);
        defaults.put(ConfigHelper.BooleanSettings.TERR80.toString(), true);

        try {
            spireConfig = new SpireConfig("Info Mod", "infoModConfig", defaults);
            // NOTE: SpireConfig constructor implicitly calls .load()
            System.out.println("OJB: successfully loaded existing config");
            printAll();
        } catch (IOException e) {
            System.out.println("OJB: could not create config");
            e.printStackTrace();
        }
    }

    // DEBUG
    public void printAll() {
        for (BooleanSettings key : BooleanSettings.values()) {
            boolean val = spireConfig.getBool(key.toString());
            System.out.println(key.toString() + ": " + val);
        }
    }

    public boolean save() {
        if (spireConfig == null)
            return false;

        try {
            spireConfig.save();
            return true;
        } catch (IOException e) {
            System.out.println("OJB: could not save config");
            e.printStackTrace();
            return false;
        }
    }

    public boolean setBool(BooleanSettings key, boolean val) {
        if (spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key.toString() + " to " + val);
        spireConfig.setBool(key.toString(), val);

        printAll();

        return save();
    }

    public boolean getBool(BooleanSettings key) {
        return spireConfig.getBool(key.toString());
    }

    public static void setupConfigMenu(CustomHitboxTipItem bossTipItem, CustomHitboxTipItem deckTipItem) {
        ModPanel modPanel = new ModPanel();
        float titleY = 745.0f * Settings.scale;

        float leftColX = 400.0f * Settings.scale;
        float rightColX = 1014.0f * Settings.scale;

        float firstDescY = 661.0f * Settings.scale;
        float itemOffsetY = 144.0f * Settings.scale; // 130.0 height / 14 gap

        modPanel.addUIElement(new InfoModConfigWrappedLabel("Info Mod Config", leftColX, titleY, Settings.CREAM_COLOR, FontHelper.bannerFont, modPanel));
        //modPanel.addUIElement(new InfoModConfigWrappedLabel("Info mod has a selection of optional modules for customizing the level of detail provided. While in a run, mousing over a particular module will usually provide additional details in the form of a tool tip.", leftColX, titleOverviewY, Settings.CREAM_COLOR, FontHelper.tipBodyFont, modPanel));

        ModTextPanel tp = new ModTextPanel();
        ModButton mb = new ModButton(300.0f, 300.0f, modPanel, modButton -> {
            tp.show(modPanel,
                    "curr",
                    "default",
                    "explan",
                    modTextPanel -> {
                        System.out.println("OJB: mod text panel cancel");
                    },
                    modTextPanel -> {
                        System.out.println("OJB: mod text panel confirm");
                    });
        });
        modPanel.addUIElement(mb);

//        modPanel.addUIElement(new ModButton(300.0f, 300.0f, modPanel, modButton -> {
//            System.out.println("OJB: mod button pressed?");
//            ModTextPanel textPanel = new ModTextPanel();
//            textPanel.show(modPanel,
//                    "Name",
//                    "!Potions: ",
//                    "Enter the text to be shown by the potion tracker.",
//                    modTextPanel -> {
//                        //cancel
//                        System.out.println("OJB: mod text panel cancel");
//                        //modTextPanel.cancel();
//                    },
//                    modTextPanel -> {
//                        System.out.println("OJB: mod text panel confirm");
//                        System.out.println("OJB: text is " + ModTextPanel.textField);
//                        //modTextPanel.confirm();
//                    } );
//        }));

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY,
                "Monster Compendium",
                "Right click an enemy while in combat to see their AI and moveset. Right click again to close this overlay.",
                modPanel,
                ConfigHelper.BooleanSettings.SHOW_MONSTER_DETAILS
        ));

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - itemOffsetY,
                "Potion Chance Tracker",
                "Displays the chance to see a potion after the next few combats. Shown as text on the top bar.",
                modPanel,
                ConfigHelper.BooleanSettings.SHOW_POTIONS
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - itemOffsetY - itemOffsetY,
                "Event Chance Tracker",
                "Displays the possible events you can get in the remaining question mark floors of the act. Shown as a [?] box on the top bar.",
                modPanel,
                ConfigHelper.BooleanSettings.SHOW_QBOX
        ));

        // Second column
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY,
                "Map Tool Tip Override (Show Bosses)",
                "Mousing over the map icon in the top right now shows the bosses you face throughout the run.",
                modPanel,
                ConfigHelper.BooleanSettings.SHOW_MAP_TIP,
                modToggleButton -> {
                    bossTipItem.enabled = modToggleButton.enabled;
                }
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY - itemOffsetY,
                "Deck Tool Tip Override",
                "Mousing over the deck icon in the top right now shows the contents of your deck in a quick access tool tip.",
                modPanel,
                ConfigHelper.BooleanSettings.SHOW_DECK_TIP,
                modToggleButton -> {
                    deckTipItem.enabled = modToggleButton.enabled;
                }
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY - itemOffsetY - itemOffsetY,
                "Special 80% Potion Chance Effect",
                "Inspired by twitch.tv/terrenceMHS",
                modPanel,
                ConfigHelper.BooleanSettings.TERR80
        ));

        BaseMod.registerModBadge(new Texture("images/icon_32.png"),
                "Info Mod",
                "ojb",
                "Displays tedious to calculate information",
                modPanel);
    }
}
