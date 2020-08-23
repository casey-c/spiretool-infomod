package InfoMod.utils.saveable;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.JsonElement;

@SpireInitializer
public class SaveableManager {
    public static BossStringsSaveable bosses = new BossStringsSaveable();
    //public static CustomPotionChanceSaveable potionChanceCustom = new CustomPotionChanceSaveable();

    public static void initialize() {
        new SaveableManager();
    }

    public static JsonElement save() {
        return new SaveHelper()
                .add(bosses)
                //.add(potionChanceCustom)
                .build();
    }

    public static void load(JsonElement jsonElement) {
        new SaveHelper(jsonElement)
                .load(bosses);
                //.load(potionChanceCustom);
    }

}
