package InfoMod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SaveHelper {
    private JsonObject constructedJson;
    private JsonElement loadedJson;

    public SaveHelper() {
        constructedJson = new JsonObject();
    }

    public SaveHelper(JsonElement loadElement) {
        this.loadedJson = loadElement;
    }

    public SaveHelper add(VeryCustomSavable saveable) {
        if (saveable == null)
            return this;

        constructedJson.add(saveable.getID(), saveable.toJson());

        return this;
    }

    public JsonElement build() {
        return constructedJson;
    }

    public SaveHelper load(VeryCustomSavable saveable) {
        System.out.println("OJB: SaveHelper::load started");
        if (saveable == null || loadedJson == null)
            return this;

        if (!loadedJson.isJsonObject())
            return this;

        System.out.println("OJB: SaveHelper::load here!");

        JsonObject obj = loadedJson.getAsJsonObject();
        if (obj.has(saveable.getID())) {
            System.out.println("OJB: SaveHelper::load has id " + saveable.getID());
            saveable.fromJson(obj.get(saveable.getID()));
        }
        else {
            System.out.println("OJB: SaveHelper::load does not have id " + saveable.getID());
        }

        System.out.println("*********************************************");
        System.out.println();
        System.out.println();

        return this;
    }

}
