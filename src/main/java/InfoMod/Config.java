package InfoMod;

import basemod.BaseMod;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.io.IOException;
import java.util.Properties;

public class Config {
    public enum ConfigOptions {
        // booleans
        SHOW_QBOX("SHOW_QBOX"),
        SHOW_MONSTER_DETAILS("SHOW_MONSTER_DETAILS"),
        SHOW_POTIONS("SHOW_POTIONS"),
        SHOW_DECK_TIP("SHOW_DECK_TIP"),
        SHOW_MAP_TIP("SHOW_MAP_TIP"),
        TERR80("TERR80"),

        // strings
        POTION_TEXT("POTION_TEXT"),

        // ints
        POTION_X("POTION_X"),
        POTION_Y("POTION_Y");

        private final String val;
        private ConfigOptions(String val) { this.val = val; }
        @Override public String toString() { return val; }
    }

    // Singleton pattern
    private static class ConfigHolder {
        private static final Config INSTANCE = new Config();
    }

    private static Config getInstance() {
        return Config.ConfigHolder.INSTANCE;
    }

    //--------------------------------------------------------------------------------

    private Properties defaults;
    private SpireConfig spireConfig;

    public Config() {
        String trueString = Boolean.toString(true);

        defaults = new Properties();
        defaults.put(ConfigOptions.SHOW_QBOX.toString(), trueString);
        defaults.put(ConfigOptions.SHOW_MONSTER_DETAILS.toString(), trueString);
        defaults.put(ConfigOptions.SHOW_POTIONS.toString(), trueString);
        defaults.put(ConfigOptions.SHOW_DECK_TIP.toString(), trueString);
        defaults.put(ConfigOptions.SHOW_MAP_TIP.toString(), trueString);
        defaults.put(ConfigOptions.TERR80.toString(), trueString);

        defaults.put(ConfigOptions.POTION_TEXT.toString(), "Potions: ");
        defaults.put(ConfigOptions.POTION_X.toString(), Integer.toString(1494));
        defaults.put(ConfigOptions.POTION_Y.toString(), Integer.toString(1056));

        try {
            spireConfig = new SpireConfig("Info Mod", "infoModConfig", defaults);
            // NOTE: SpireConfig constructor implicitly calls .load()
            System.out.println("OJB: successfully loaded existing config");
            printDefaults();
        } catch (IOException e) {
            System.out.println("OJB: could not create config");
            e.printStackTrace();
        }
    }

    // DEBUG
    private void printDefaults() {
        for (ConfigOptions option : ConfigOptions.values()) {
            String v = defaults.getProperty(option.toString());
            System.out.println("OJB: config has " + option.toString() + ": " + v);
        }

    }

    //--------------------------------------------------------------------------------

    public static boolean setBoolean(String key, boolean val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key + " to " + val);
        config.spireConfig.setBool(key, val);

        return config.save();
    }

    public static boolean setString(String key, String val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key + " to " + val);
        config.spireConfig.setString(key, val);

        return config.save();
    }

    public static boolean setInt(String key, int val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key + " to " + val);
        config.spireConfig.setInt(key, val);

        return config.save();
    }

    //--------------------------------------------------------------------------------

    public static boolean getBool(String key) {
        Config config = getInstance();

        if (config.spireConfig == null || !config.spireConfig.has(key))
            return false;

        return config.spireConfig.getBool(key);
    }

    public static String getString(String key) {
        Config config = getInstance();

        if (config.spireConfig == null || !config.spireConfig.has(key))
            return "";

        return config.spireConfig.getString(key);
    }

    public static int getInt(String key) {
        Config config = getInstance();

        if (config.spireConfig == null || !config.spireConfig.has(key))
            return 0;

        return config.spireConfig.getInt(key);
    }

    //--------------------------------------------------------------------------------

    public static String getDefaultString(String key) {
        Config config = getInstance();
        return (String)config.defaults.getProperty(key);
    }

    public static int getDefaultInt(String key) {
        Config config = getInstance();
        return Integer.parseInt(config.defaults.getProperty(key));
    }

    //--------------------------------------------------------------------------------

    private boolean save() {
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

    //--------------------------------------------------------------------------------

    public static void setupConfigMenu(CustomHitboxTipItem bossTipItem, CustomHitboxTipItem deckTipItem) {
        ModPanel modPanel = new ModPanel();
        float titleY = 745.0f * Settings.scale;

        float leftColX = 400.0f * Settings.scale;
        float rightColX = 1014.0f * Settings.scale;

        float firstDescY = 661.0f * Settings.scale;
        float itemOffsetY = 144.0f * Settings.scale; // 130.0 height / 14 gap

        modPanel.addUIElement(new InfoModConfigWrappedLabel("Info Mod Config", leftColX, titleY, Settings.CREAM_COLOR, FontHelper.bannerFont, modPanel));

//        ModTextPanel tp = new ModTextPanel();
//        ModButton mb = new ModButton(300.0f, 300.0f, modPanel, modButton -> {
//            tp.show(modPanel,
//                    "curr",
//                    "default",
//                    "explan",
//                    modTextPanel -> {
//                        System.out.println("OJB: mod text panel cancel");
//                    },
//                    modTextPanel -> {
//                        System.out.println("OJB: mod text panel confirm");
//                    });
//        });
//        modPanel.addUIElement(mb);

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY,
                "Monster Compendium",
                "Right click an enemy while in combat to see their AI and moveset. Right click again to close this overlay.",
                modPanel,
                ConfigOptions.SHOW_MONSTER_DETAILS
                //ConfigHelper.BooleanSettings.SHOW_MONSTER_DETAILS
        ));

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - itemOffsetY,
                "Potion Chance Tracker",
                "Displays the chance to see a potion after the next few combats. Shown as text on the top bar.",
                modPanel,
                ConfigOptions.SHOW_POTIONS
                //ConfigHelper.BooleanSettings.SHOW_POTIONS
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - itemOffsetY - itemOffsetY,
                "Event Chance Tracker",
                "Displays the possible events you can get in the remaining question mark floors of the act. Shown as a [?] box on the top bar.",
                modPanel,
                ConfigOptions.SHOW_QBOX
                //ConfigHelper.BooleanSettings.SHOW_QBOX
        ));

        // Second column
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY,
                "Map Tool Tip Override (Show Bosses)",
                "Mousing over the map icon in the top right now shows the bosses you face throughout the run.",
                modPanel,
                ConfigOptions.SHOW_MAP_TIP,
                //ConfigHelper.BooleanSettings.SHOW_MAP_TIP,
                modToggleButton -> {
                    bossTipItem.enabled = modToggleButton.enabled;
                }
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY - itemOffsetY,
                "Deck Tool Tip Override",
                "Mousing over the deck icon in the top right now shows the contents of your deck in a quick access tool tip.",
                modPanel,
                ConfigOptions.SHOW_DECK_TIP,
                //ConfigHelper.BooleanSettings.SHOW_DECK_TIP,
                modToggleButton -> {
                    deckTipItem.enabled = modToggleButton.enabled;
                }
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                rightColX, firstDescY - itemOffsetY - itemOffsetY,
                "Special 80% Potion Chance Effect",
                "Inspired by twitch.tv/terrenceMHS",
                modPanel,
                ConfigOptions.TERR80
                //ConfigHelper.BooleanSettings.TERR80
        ));

        BaseMod.registerModBadge(new Texture("images/icon_32.png"),
                "Info Mod",
                "ojb",
                "Displays tedious to calculate information",
                modPanel);
    }
}
