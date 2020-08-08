package InfoMod;

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

// TODO: load from JSON
// TODO: special fixes for jaw worm (regular vs hard version in act 3 -- both have same id but different AI)

@SpireInitializer
public class MonsterInfoDatabase implements PostInitializeSubscriber {
    private static HashMap<String, MonsterInfo> monsters = new HashMap<String, MonsterInfo>();

    public MonsterInfoDatabase() {
        BaseMod.subscribe(this);
    }

    private void setupTestMonsters() {
        MonsterInfo null_monster = new MonsterInfo("NULL", "404: Monster Not Found", "", "None")
                .with_ai("This monster is not yet in our database. If this is a modded monster, you will need to add it in manually (check out the InfoMod github page to see how). If this is a base game monster, this is a bug and should be reported. Sorry!") ;

//        MonsterInfo jaw_worm = new MonsterInfo("JawWorm", "Jaw Worm", "(42 - 46 HP)", "None")
//                .with_move(new MonsterInfo.Move("Bellow").d0("Str +5", RenderingUtils.OJB_BUFF_COLOR).d1("Block 9", RenderingUtils.OJB_BLOCK_COLOR))
//                .with_move(new MonsterInfo.Move("Thrash").d0("7", RenderingUtils.OJB_DAMAGE_COLOR))
//                .with_move(new MonsterInfo.Move("Chomp").d0("12", RenderingUtils.OJB_DAMAGE_COLOR))
//                .with_ai("Always starts with #wChomp. Afterwards, has a 45-30-25 split between #wBellow, #wThrash and #wChomp, but cannot use #wThrash three times in a row or #wChomp or #wBellow twice in a row.\n") ;
//
//        MonsterInfo red_louse = new MonsterInfo("FuzzyLouseNormal", "Red Louse", "(11 - 16 HP)", "Innate Curl Up #bC, where #bC is random [9, 12]. #rD is random [6, 8]")
//                .with_move(new MonsterInfo.Move("Grow").d0("Str +4", RenderingUtils.OJB_BUFF_COLOR))
//                .with_move(new MonsterInfo.Move("Bite").d0("D", RenderingUtils.OJB_DAMAGE_COLOR))
//                .with_ai("25% chance of #wGrow and 75% of #wBite, but cannot #wGrow twice in a row or #wBite 3 times in a row") ;
//
//        MonsterInfo green_louse = new MonsterInfo("FuzzyLouseDefensive", "Green Louse", "(12 - 18 HP)", "Innate Curl Up #bC, where #bC is random [9, 12]. #rD is random [6, 8]")
//                .with_move(new MonsterInfo.Move("Spit Web").d0("Weak 2", RenderingUtils.OJB_DEBUFF_COLOR))
//                .with_move(new MonsterInfo.Move("Bite").d0("D", RenderingUtils.OJB_DAMAGE_COLOR))
//                .with_ai("25% chance of #wSpit #wWeb and 75% of #wBite, but cannot #wSpit #wWeb twice in a row or #wBite 3 times in a row") ;

        monsters.put("NULL", null_monster);
//        monsters.put("JawWorm", jaw_worm);
//        monsters.put("FuzzyLouseNormal", red_louse);
//        monsters.put("FuzzyLouseDefensive", green_louse);
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

    public void printAll() {
        System.out.println("***************************************");
        System.out.println("OJB: Monster Database has " + monsters.size() + " elements");
        for (MonsterInfo m : monsters.values()) {
            m.print();
        }
        System.out.println("***************************************");
    }

    @Override
    public void receivePostInitialize() {
        setupTestMonsters();

        System.out.println("OJB: initialized monster database");

        // Load JSON tests
        InputStream in = getClass().getClassLoader().getResourceAsStream("monsters.json");
        if (in == null) {
            System.out.println("OJB ERROR: couldn't read monsters.json to a string - so no AI information :( [input stream null]");
        }
        else {
            try {
                String content = MiscUtils.resourceStreamToString(in);

                JsonObject obj = new JsonParser().parse(content).getAsJsonObject();
                if (obj.has("monsters") && obj.get("monsters").isJsonArray()) {
                    JsonArray list = obj.getAsJsonArray("monsters");

                    System.out.println("Found a json list: " + list);

                    for (JsonElement elt : list) {
                        if (!elt.isJsonObject()) {
                            System.out.println("elt " + elt + "\n\tIs not a json object, so skipping");
                            continue;
                        }

                        MonsterInfo monster = MonsterInfo.constructFromJSON(elt.getAsJsonObject());
                        if (monster != null) {
                            System.out.println("monster is not null!");
                            System.out.println(monster);

                            monsters.put(monster.getId(), monster);
                        }
                        else {
                            System.out.println("monster was null, not putting in database");
                        }
                    }
                }


            } catch (IOException e) {
                System.out.println("OJB ERROR: couldn't read monsters.json to a string - so no AI information :(");
                e.printStackTrace();
            }

        }

        //System.out.println("OJB: monster database successfully built. Now has: " + monsters.size() + " monsters");
        printAll();

//        File file = new File( getClass().getClassLoader().getResource(json_location).getFile() );
//        if (file.exists()) {
//            System.out.println("OJB: monsters.json exists! hooray");
//        }
//        else {
//            System.out.println("OJB: monsters.json does not exist :(");
//        }
    }
}
