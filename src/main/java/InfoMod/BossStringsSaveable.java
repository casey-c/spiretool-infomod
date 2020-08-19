package InfoMod;

import basemod.abstracts.CustomSavableRaw;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue.Save;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BossStringsSaveable implements ISavable {
    public String act1, act2, act3_1, act3_2;
    public String combined;

    private static final String ID = "OJB_BOSS_STRINGS";

    private BossStringsSaveable() { }

    public static BossStringsSaveable get() {
        ISavable existing = SaveManager.getSavable(ID);

        if (existing == null) {
            BossStringsSaveable res = new BossStringsSaveable();
            JsonElement raw = SaveManager.getSaveableAsJson(ID);

            if (raw != null)
                res.fromJsonElement(raw);

            SaveManager.setSavable(ID, res);
            return res;
        }
        else {
            return (BossStringsSaveable)existing;
        }

    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public JsonElement toJsonEleement() {
        JsonArray elt = new JsonArray();
        elt.add(act1);
        elt.add(act2);
        elt.add(act3_1);
        elt.add(act3_2);
        elt.add(combined);
        return elt;
    }

    @Override
    public void fromJsonElement(JsonElement elt) {
        if (!elt.isJsonArray()) {
            System.out.println("OJB ERROR: could not load boss strings (not json array)");
            return;
        }

        JsonArray arr = (JsonArray)elt;

        if (arr.size() == 5) {
            act1 = arr.get(0).getAsString();
            act2 = arr.get(0).getAsString();
            act3_1 = arr.get(0).getAsString();
            act3_2 = arr.get(0).getAsString();
            combined = arr.get(0).getAsString();
        }
        else {
            System.out.println("OJB ERROR: could not load boss strings (wrong size)");
        }
    }

}
