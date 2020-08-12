package InfoMod;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import java.io.IOException;
import java.util.Properties;

/*
    A wrapper class for SpireConfig. InfoMod specific options can be reached from the ConfigSettings enum
 */
public class ConfigHelper {
    private Properties defaults;
    private SpireConfig spireConfig;

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

    ConfigHelper() {
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
}
