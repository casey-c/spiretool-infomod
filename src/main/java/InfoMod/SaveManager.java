package InfoMod;

import basemod.BaseMod;
import basemod.abstracts.CustomSavableRaw;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/*
TODO: This is just an experiment for myself, and should not be imitated as it's probably bad. (At this point in the
  over-engineering phase, I have a ton of doubts towards the usability).

The idea: the manager will keep track of a master json object with the following pieces:
  * an id array of all savable objects (these are the ID of ISavable implementations -- unique per class please!)
    currently saved in the overall json
  * key,value pairs where the keys are each id in the previous array, and the value is a jsonobject of its own
    corresponding to the save data for that particular ISavable (i.e. from ISavable::toJsonElement())

We want to make it flexible enough and easy to add new savable data whenever and from wherever, just with access to
this static singleton manager object.

Classes in charge of an ISavable should try and see if the SaveManager has the savable they need (getSavable(id)),
and if that fails (return null - perhaps the savable is not yet built) -- to then try and see if the raw json data
was at least loaded by the manager (getSaveableAsJson()). they can then use that raw json to build the ISavable object
and tell the savemanager to store it in the real data map for future use and saving requirements.

If that second function returns null as well, the ISavable is being used for the first time and therefore wasn't loaded
from a preexisting config file. The class in charge of the savable should then create it with the default values
desired, and then tell the manager about it using the setSavable() function.

TL;DR:
* Classes which need to save / load data should have an ISavable inheriting member (ISavables should have unique ids)
* When the data is needed to be read, classes should first attempt to getSavable() to load the data
    [*] If that fails, they should then try to getSavableAsJson() to see if the data exists in raw form.
        Upon reading the raw data, they should build the real java ISavable object and then tell the manager
        to setSavable() to start tracking it for later (and for saving in the future)
    [*] If that raw form also doesn't exist, they should create a new() ISavable with the defaults they need, and then
       tell the manager to setSavable() with that data so they won't have to do it again.

Note: this design is probably trash and should maybe be reworked with some better design patterns. We'll see how it
turns out first. It may just need some better helper methods in the future, but honestly I think it has some fundamental
flaws. I originally wanted to try and do something different than the CustomSavableRaw implementation in every class,
as it sort of leads to some very messy code which violates the SRP. I'd rather have saver classes know how to save
then require every class to know how to convert to / from json on its own as the classes get cluttered pretty easily.


 */

@SpireInitializer
public class SaveManager implements CustomSavableRaw {
    private static SaveManager instance;
    private static HashMap<String, ISavable> data;
    private static HashMap<String, JsonElement> raw_data;

    private static final String MANAGER_IDS = "MANAGER_IDS";
    private static boolean successfullyLoaded = false;

    public static void initialize() {
        instance = new SaveManager();
    }

    public SaveManager() {
        BaseMod.addSaveField("ojb_infomod", this);

        data = new HashMap<String, ISavable>();
        raw_data = new HashMap<String, JsonElement>();
    }

    public static boolean isSuccessfullyLoaded() {
        return successfullyLoaded;
    }

    private static SaveManager getInstance() {
        if (instance == null)
            instance = new SaveManager();

        return instance;
    }

    public static @Nullable
    ISavable getSavable(String id) {
        if (!data.containsKey(id))
            return null;
        else
            return data.get(id);
    }

    public static @Nullable
    JsonElement getSaveableAsJson(String id) {
        if (!raw_data.containsKey(id))
            return null;
        else
            return raw_data.get(id);
    }

    public static void setSavable(String id, ISavable saveable) {
        data.put(id, saveable);
    }

    @Override
    public JsonElement onSaveRaw() {
        JsonObject elt = new JsonObject();
        JsonArray ids = new JsonArray();

        for (Map.Entry<String, ISavable> entry : data.entrySet()) {
            String key = entry.getKey();
            ISavable saveable = entry.getValue();

            elt.add(key, saveable.toJsonEleement());
            ids.add(key);
        }

        elt.add(MANAGER_IDS, ids);
        System.out.println("OJB: Save manager saved " + ids.size() + " ISavable elements");
        return elt;
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        System.out.println("OJB: Save manager attempting to load.");

        if (jsonElement == null) {
            System.out.println("OJB ERROR: jsonElement null!");
            return;
        }


        if (!jsonElement.isJsonObject())
            return;

        JsonObject obj = (JsonObject) jsonElement;

        if (!obj.has(MANAGER_IDS))
            return;

        JsonArray ids = obj.getAsJsonArray(MANAGER_IDS);
        System.out.println("OJB: Save manager has " + ids.size() + " ids");

        for (JsonElement id_elt : ids) {
            String id = id_elt.getAsString();
            JsonElement saveable_obj = obj.get(id);
            raw_data.put(id, saveable_obj);

        }

        successfullyLoaded = true;
//        if (jsonElement.isJsonArray()) {
//            JsonArray arr = jsonElement.getAsJsonArray();
//
//            // probably don't need to do all this checking lol
//            if (arr.size() == 5) {
//                if (arr.get(0).isJsonPrimitive())
//                    bossStringsSaveable.act1 = arr.get(0).getAsString();
//                if (arr.get(1).isJsonPrimitive())
//                    bossStringsSaveable.act2 = arr.get(1).getAsString();
//                if (arr.get(2).isJsonPrimitive())
//                    bossStringsSaveable.act3_1 = arr.get(2).getAsString();
//                if (arr.get(3).isJsonPrimitive())
//                    bossStringsSaveable.act3_2 = arr.get(3).getAsString();
//                if (arr.get(4).isJsonPrimitive())
//                    bossStringsSaveable.combined = arr.get(4).getAsString();
//
//                // Make sure to set the actual thing
//                bossTipItem.setPrimaryTipBody(bossStringsSaveable.combined);
//            }
//        }
    }
}
