package InfoMod.modules.compendium;

import InfoMod.utils.MiscUtils;
import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/*
  This class is responsible for loading all the monster details (ai/moveset) from monster.json at the launch of the game.
  The MonsterInfoOverlay will try and load the proper details from this class's hashmap to show to the player.
 */
@SpireInitializer
public class MonsterInfoDatabase implements PostInitializeSubscriber {
    private static HashMap<String, MonsterInfo> monsters = new HashMap<String, MonsterInfo>();

    public MonsterInfoDatabase() {
        BaseMod.subscribe(this);
    }

    private void setup() {
        // Put in the default text for when we encounter a monster with an ID not in our database yet.
        MonsterInfo null_monster = new MonsterInfo("NULL",
                "404: Monster Not Found",
                "",
                "This monster is not yet in our database. If this is a modded monster, you will need to add it in manually (check out the InfoMod github page to see how). If this is a base game monster, this is a bug and should be reported. Sorry!",
                "None"
                );

        monsters.put("NULL", null_monster);
    }

    public static boolean hasMonsterID(String id) {
        return monsters.containsKey(id);
    }

    public static @Nullable
    MonsterInfo getMonsterByID(String id) {
        // TODO: verify if this contains check is even required (will get just work and return null?)
        if (monsters.containsKey(id))
            return monsters.get(id);
        else
            return null;
    }

    public static void initialize() {
        new MonsterInfoDatabase();
    }

    // DEBUG
    public void printAll() {
        System.out.println("***************************************");
        System.out.println("OJB: Monster Database has " + monsters.size() + " elements");
        System.out.println();

        System.out.println("{");
        System.out.println("\"monsters\": [");

        for (MonsterInfo m : monsters.values()) {
            m.print();
        }

        System.out.println("]");

        System.out.println("}");

        System.out.println();
        System.out.println("***************************************");
    }

    @Override
    public void receivePostInitialize() {
        setup();

        // Load the monsters.json (located in the resources folder of this code)
        InputStream in = getClass().getClassLoader().getResourceAsStream("monsters.json");
        if (in != null) {
            try {
                String content = MiscUtils.resourceStreamToString(in);

                JsonObject obj = new JsonParser().parse(content).getAsJsonObject();
                if (obj.has("monsters") && obj.get("monsters").isJsonArray()) {
                    JsonArray list = obj.getAsJsonArray("monsters");

                    for (JsonElement elt : list) {
                        if (!elt.isJsonObject())
                            continue;

                        MonsterInfo monster = MonsterInfo.constructFromJSON(elt.getAsJsonObject());
                        if (monster != null)
                            monsters.put(monster.getId(), monster);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("OJB ERROR: could not load monsters.json");
        }

        //printAll();
    }
}
