package InfoMod.utils.config;

import InfoMod.ui.labels.CustomHitboxTipItem;
import basemod.BaseMod;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

public class Config {
    public enum ConfigOptions {
        // booleans
        SHOW_QBOX("SHOW_QBOX"),
        SHOW_MONSTER_DETAILS("SHOW_MONSTER_DETAILS"),
        SHOW_POTIONS("SHOW_POTIONS"),
        SHOW_DECK_TIP("SHOW_DECK_TIP"),
        SHOW_MAP_TIP("SHOW_MAP_TIP"),
        TERR80("TERR80"),
        SHOW_POTION_IMG("SHOW_POTION_IMG"),
        MONSTER_OVERLAY_REQ_SHIFT("MONSTER_OVERLAY_REQ_SHIFT"),

        // strings
        POTION_TEXT("POTION_TEXT"),

        // ints
        POTION_X("POTION_X"),
        POTION_Y("POTION_Y");

        private final String val;
        ConfigOptions(String val) { this.val = val; }
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
        defaults.put(ConfigOptions.SHOW_POTION_IMG.toString(), Boolean.toString(false));

        defaults.put(ConfigOptions.MONSTER_OVERLAY_REQ_SHIFT.toString(), trueString);

        defaults.put(ConfigOptions.POTION_TEXT.toString(), "Potions: ");
        defaults.put(ConfigOptions.POTION_X.toString(), Integer.toString((int)(1494 * Settings.scale)));
        defaults.put(ConfigOptions.POTION_Y.toString(), Integer.toString((int)(1056 * Settings.scale)));

        try {
            spireConfig = new SpireConfig("Info Mod", "infoModConfig", defaults);
            // NOTE: SpireConfig constructor implicitly calls .load()
            System.out.println("OJB: successfully loaded existing config");
            printDefaults();
            printAll();

            spireConfig.save();

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

    private void printAll() {
        System.out.println("OJB: config current values are: ");
        Consumer<ConfigOptions> printBool = o -> {
            boolean b = spireConfig.getBool(o.toString());
            System.out.println(o.toString() + ": " + b);
        };
        Consumer<ConfigOptions> printString = o -> {
            String s = spireConfig.getString(o.toString());
            System.out.println(o.toString() + ": " + s);
        };
        Consumer<ConfigOptions> printInt = o -> {
            int i = spireConfig.getInt(o.toString());
            System.out.println(o.toString() + ": " + i);
        };

        printBool.accept(ConfigOptions.SHOW_QBOX);
        printBool.accept(ConfigOptions.SHOW_MONSTER_DETAILS);
        printBool.accept(ConfigOptions.SHOW_POTIONS);
        printBool.accept(ConfigOptions.SHOW_DECK_TIP);
        printBool.accept(ConfigOptions.SHOW_MAP_TIP);
        printBool.accept(ConfigOptions.TERR80);
        printBool.accept(ConfigOptions.SHOW_POTION_IMG);
        printBool.accept(ConfigOptions.MONSTER_OVERLAY_REQ_SHIFT);

        printString.accept(ConfigOptions.POTION_TEXT);

        printInt.accept(ConfigOptions.POTION_X);
        printInt.accept(ConfigOptions.POTION_Y);
    }

    public static void print() {
        Config config = getInstance();
        if (config.spireConfig == null)
            return;

        config.printAll();
    }

    //--------------------------------------------------------------------------------

    public static boolean setBoolean(ConfigOptions key, boolean val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key.toString() + " to " + val);
        config.spireConfig.setBool(key.toString(), val);

        return config.save();
    }

    public static boolean setString(ConfigOptions key, String val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key.toString() + " to " + val);
        config.spireConfig.setString(key.toString(), val);

        return config.save();
    }

    public static boolean setInt(ConfigOptions key, int val) {
        Config config = getInstance();

        if (config.spireConfig == null)
            return false;

        System.out.println("OJB: config is setting " + key.toString() + " to " + val);
        config.spireConfig.setInt(key.toString(), val);

        return config.save();
    }

    //--------------------------------------------------------------------------------

    // TODO: verify that the config writes correctly when .has() fails
    // TODO: use my setters above? (they do the same thing but duplicated)
    // TODO: cleanup / refactor this whole class

    public static boolean getBool(ConfigOptions key) {
        Config config = getInstance();

        if (config.spireConfig == null) {
            System.out.println("OJB WARNING: SpireConfig null! This shouldn't happen --------------------- ");
            return getDefaultBool(key);
        }

        if (!config.spireConfig.has(key.toString())) {
            System.out.println("OJB: spire config does not have " + key.toString() + ". Adding now.");
            config.spireConfig.setBool(key.toString(), getDefaultBool(key));
            config.save();
        }

        return config.spireConfig.getBool(key.toString());
    }

    public static String getString(ConfigOptions key) {
        Config config = getInstance();

        if (config.spireConfig == null) {
            System.out.println("OJB WARNING: SpireConfig null! This shouldn't happen --------------------- ");
            return getDefaultString(key);
        }

        if (!config.spireConfig.has(key.toString())) {
            System.out.println("OJB: spire config does not have " + key.toString() + ". Adding now.");
            config.spireConfig.setString(key.toString(), getDefaultString(key));
            config.save();
        }

//        if (config.spireConfig == null || !config.spireConfig.has(key.toString()))
//            return "";

        return config.spireConfig.getString(key.toString());
    }

    public static int getInt(ConfigOptions key) {
        Config config = getInstance();

//        if (config.spireConfig == null || !config.spireConfig.has(key.toString()))
//            return 0;

        if (config.spireConfig == null) {
            System.out.println("OJB WARNING: SpireConfig null! This shouldn't happen --------------------- ");
            return getDefaultInt(key);
        }

        if (!config.spireConfig.has(key.toString())) {
            System.out.println("OJB: spire config does not have " + key.toString() + ". Adding now.");
            config.spireConfig.setInt(key.toString(), getDefaultInt(key));
            config.save();
        }

        return config.spireConfig.getInt(key.toString());
    }

    //--------------------------------------------------------------------------------

    public static boolean getDefaultBool(ConfigOptions key) {
        Config config = getInstance();
        return Boolean.parseBoolean(config.defaults.getProperty(key.toString()));
    }

    public static String getDefaultString(ConfigOptions key) {
        Config config = getInstance();
        return config.defaults.getProperty(key.toString());
    }

    public static int getDefaultInt(ConfigOptions key) {
        Config config = getInstance();
        return Integer.parseInt(config.defaults.getProperty(key.toString()));
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
        //ModPanel modPanel = new ModPanel();
        BetterModPanel modPanel = new BetterModPanel();

        float titleY = 889.0f;

        float leftColX = 400.0f;
        float rightColX = 1014.0f;

        float firstDescY = 805.0f;
        //float itemOffsetY = 144.0f * Settings.scale; // 130.0 height / 14 gap
        float itemOffsetY = 174.0f; // 130.0 height / 14 gap

        modPanel.addUIElement(new InfoModConfigWrappedLabel("Info Mod Config", leftColX, titleY, Settings.CREAM_COLOR, FontHelper.bannerFont, modPanel));

        // LEFT COLUMN --------------------------------------------------------------------------------
        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY,
                "Monster Compendium",
                "Right click an enemy while in combat to see their AI and moveset. Right click again to close this overlay.",
                modPanel,
                ConfigOptions.SHOW_MONSTER_DETAILS
        ));

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - itemOffsetY,
                "Monster Compendium -- Require Shift",
                "The monster compendium requires SHIFT to be held while right clicking to open the overlay. (Prevents accidental openings)",
                modPanel,
                ConfigOptions.MONSTER_OVERLAY_REQ_SHIFT
        ));

        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - 2 * itemOffsetY,
                "Potion Chance Tracker",
                "Displays the chance to see a potion after the next few combats. Shown as text on the top bar.",
                modPanel,
                ConfigOptions.SHOW_POTIONS
                //ConfigHelper.BooleanSettings.SHOW_POTIONS
        ));
        modPanel.addUIElement(new InfoModConfigDescBool(
                leftColX, firstDescY - 3 * itemOffsetY,
                "Event Chance Tracker",
                "Displays the possible events you can get in the remaining question mark floors of the act. Shown as a [?] box on the top bar.",
                modPanel,
                ConfigOptions.SHOW_QBOX
                //ConfigHelper.BooleanSettings.SHOW_QBOX
        ));

        // RIGHT COLUMN --------------------------------------------------------------------------------
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
