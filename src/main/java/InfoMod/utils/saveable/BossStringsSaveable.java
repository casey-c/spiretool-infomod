package InfoMod.utils.saveable;

import InfoMod.InfoMod;
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
        JsonObject elt = new JsonObject();
        elt.addProperty("act1", act1);
        elt.addProperty("act2", act2);
        elt.addProperty("act3_1", act3_1);
        elt.addProperty("act3_2", act3_2);
        elt.addProperty("combined", combined);
        return elt;
    }

    @Override
    public void fromJson(JsonElement elt) {
        if (elt == null || !elt.isJsonObject())
            return;

        JsonObject obj = elt.getAsJsonObject();
        act1 = obj.has("act1") ? obj.get("act1").getAsString() : "";
        act2 = obj.has("act2") ? obj.get("act2").getAsString() : "";
        act3_1 = obj.has("act3_1") ? obj.get("act3_1").getAsString() : "";
        act3_2 = obj.has("act3_2") ? obj.get("act3_2").getAsString() : "";
        combined = obj.has("combined") ? obj.get("combined").getAsString() : "";
    }

}
