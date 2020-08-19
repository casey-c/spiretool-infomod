package InfoMod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class VeryCustomSavable {
    protected String ID;
    public String getID() { return ID; }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement elt);
}
