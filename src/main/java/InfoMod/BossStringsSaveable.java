package InfoMod;

import basemod.abstracts.CustomSavableRaw;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue.Save;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BossStringsSaveable extends VeryCustomSavable {
    public String act1, act2, act3_1, act3_2;
    public String combined;

    public BossStringsSaveable() {
        this.ID = "BOSS_STRINGS";
    }

    @Override
    public JsonElement toJson() {
        JsonArray elt = new JsonArray();
        elt.add(act1);
        elt.add(act2);
        elt.add(act3_1);
        elt.add(act3_2);
        elt.add(combined);
        return elt;
    }

    @Override
    public void fromJson(JsonElement elt) {
        if (!elt.isJsonArray()) {
            System.out.println("OJB ERROR: could not load boss strings (not json array)");
            return;
        }

        JsonArray arr = (JsonArray)elt;
        System.out.println("BOSS STRINGS SAVEABLE FROM JSON: ");
        System.out.println(arr);
        System.out.println("*************************");
        System.out.println();
        System.out.println();
        System.out.println("boss strings saveable.size " + arr.size());

        if (arr.size() == 5) {
            act1 = (arr.get(0).isJsonPrimitive()) ? arr.get(0).getAsString() : "";
            act2 = (arr.get(1).isJsonPrimitive()) ? arr.get(1).getAsString() : "";
            act3_1 = (arr.get(2).isJsonPrimitive()) ? arr.get(2).getAsString() : "";
            act3_2 = (arr.get(3).isJsonPrimitive()) ? arr.get(3).getAsString() : "";
            combined = (arr.get(4).isJsonPrimitive()) ? arr.get(4).getAsString() : "";
        }
        else {
            System.out.println("OJB ERROR: could not load boss strings (wrong size)");
        }
    }

}
