package InfoMod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomPotionChanceSaveable extends VeryCustomSavable {
    public static final String DEFAULT_TEXT = "Potions: ";
    public static final int DEFAULT_X = 1494;
    public static final int DEFAULT_Y = 1060;

    public String customText = DEFAULT_TEXT;
    public int x = DEFAULT_X;
    public int y = DEFAULT_Y;


    public CustomPotionChanceSaveable() {
        this.ID = "POTION_TEXT";
    }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("customText", customText);
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        return obj;
    }

    @Override
    public void fromJson(JsonElement elt) {
        if (elt == null || !elt.isJsonObject())
            return;

        JsonObject obj = elt.getAsJsonObject();

        customText = obj.has("customText") ? obj.get("customText").getAsString() : DEFAULT_TEXT;
        x = obj.has("x") ? obj.get("x").getAsInt() : DEFAULT_X;
        y = obj.has("y") ? obj.get("y").getAsInt() : DEFAULT_Y;

    }
}
