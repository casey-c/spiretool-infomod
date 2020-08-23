package InfoMod.utils.saveable;

import InfoMod.InfoMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
Be sure to set a unique ID for each saveable that extends this class (in the constructor is fine). There are
automated ways of doing it but this works as long as you set the ID appropriately
 */
public abstract class VeryCustomSavable {
    protected String ID;
    public String getID() { return ID; }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement elt);
}
