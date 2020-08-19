package InfoMod;

import com.google.gson.JsonElement;

public interface ISavable {
    public String getID();
    public JsonElement toJsonEleement();
    public void fromJsonElement(JsonElement elt);
}
