package InfoMod;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.monsters.ending.SpireShield;
import com.megacrit.cardcrawl.monsters.ending.SpireSpear;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/*
   Utility class for storing data about the monster's moveset and AI
   This data will be available to the player in game (like a built-in version of the reference spreadsheet).

   Moves are stored in a slightly convoluted format in order to help out the rendering later.

   TODO: a master MonsterDatabase class to load all the AI moveset information from JSON at the start
   TODO: an overlay class which will display the information contained in these objects when requested.
*/
public class MonsterInfo {
    // A move available in this monster's AI toolkit. The spreadsheet has space for 6 moves total in the monster AI.
    // The d strings are the details of what the move does / the effects it applies. They are spread out so each
    // move interaction can have its own font color; there are (hopefully) enough to provide ample space for longer
    // effects. (Not all detailed strings may contain content; and they may not be contiguous, e.g. d0 and d4 may be
    // the only ones if d0 has a lot of text -- this is up to the JSON creator to make a judgement to avoid overlap)
    public static class Move {
        //public boolean exists;
        public String move_name;
        public String d0, d1, d2, d3, d4, d5;
        public Color c0, c1, c2, c3, c4, c5;

        public Move(String name) {
            move_name = name;
            c0 = c1 = c2 = c3 = c4 = c5 = RenderingUtils.OJB_DARK_GRAY_COLOR;
        }

        // TODO: refactor away from builders (builders were helpful in the original testing, now they're stupid again)

        public Move d0(String d0, Color c0) {
            this.d0 = d0;
            this.c0 = c0;
            return this;
        }
        public Move d1(String d1, Color c1) {
            this.d1 = d1;
            this.c1 = c1;
            return this;
        }
        public Move d2(String d2, Color c2) {
            this.d2 = d2;
            this.c2 = c2;
            return this;
        }
        public Move d3(String d3, Color c3) {
            this.d3 = d3;
            this.c3 = c3;
            return this;
        }
        public Move d4(String d4, Color c4) {
            this.d4 = d4;
            this.c4 = c4;
            return this;
        }
        public Move d5(String d5, Color c5) {
            this.d5 = d5;
            this.c5 = c5;
            return this;
        }
    }

    // Basic details about this monster
    private String id;
    private String name;
    private String hp_string;

    // Moves available
    //private int num_moves = 0;
    //private boolean m0_exists = false, m1_exists = false, m2_exists = false, m3_exists = false, m4_exists = false, m5_exists = false;
    //private Move m0, m1, m2, m3, m4, m5;

    //private int num_moves = 0;
    //private static final int MAX_MOVES = 6;
    ArrayList<Move> moves = new ArrayList<>();

    // Describes the AI behavior / when to take each of the moves
    private String ai_description;

    // Describes additional details about the monster (e.g. does it start with innate artifact / powers? etc.)
    private String notes;

    public MonsterInfo() {
        //FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont);
    }

    public MonsterInfo(String id, String name, String hp_string, String ai, String notes) {
        this.id = id;
        this.name = name;
        this.hp_string = hp_string;
        this.ai_description = ai;
        this.notes = notes;
    }

    // TODO: remove (was originally for testing + had some builders on top)
    @Deprecated
    public MonsterInfo(String id, String name, String hp_string, String notes) {
        this.id = id;
        this.name = name;
        this.hp_string = hp_string;
        this.notes = notes;
    }

    private static Color stringToColor(String s) {
        switch (s) {
            case "DEBUFF":
                return RenderingUtils.OJB_DEBUFF_COLOR;
            case "BUFF":
                return RenderingUtils.OJB_BUFF_COLOR;
            case "DAMAGE":
                return RenderingUtils.OJB_DAMAGE_COLOR;
            case "BLOCK":
                return RenderingUtils.OJB_BLOCK_COLOR;
            case "SKIP":
                return RenderingUtils.OJB_GRAY_COLOR;
            default:
                return RenderingUtils.OJB_DARK_GRAY_COLOR;
        }
    }

    public static @Nullable MonsterInfo constructFromJSON(JsonObject obj) {
        // Monsters must have at least an ID
        if (!obj.has("id"))
            return null;

        String id = obj.get("id").getAsString();

        // The name of the monster (shown in game)
        String name = (obj.has("name")) ? obj.get("name").getAsString() : id;
        String hp_string = (obj.has("hp")) ? obj.get("hp").getAsString() : "";

        // Other details are optional (TODO: better than blank strings?)
        String ai = (obj.has("ai")) ? obj.get("ai").getAsString() : "";
        String notes = (obj.has("notes")) ? obj.get("notes").getAsString() : "None";

        MonsterInfo monster = new MonsterInfo(id, name, hp_string, ai, notes);

        // Moves are an array of move objects
        if (obj.has("moves") && obj.get("moves").isJsonArray()) {
            JsonArray moves = obj.getAsJsonArray("moves");

            for (JsonElement move : moves) {
                if (!move.isJsonObject())
                    continue;

                JsonObject m = move.getAsJsonObject();

                // Need at least a name for this move
                if (!m.has("name"))
                    continue;

                String move_name = m.get("name").getAsString();
                Move created_move = new Move(move_name);

                // Details are optional (e.g. "Split" from a slime has no effects technically but spawning)
                if (m.has("details") && m.get("details").isJsonObject()) {
                    JsonObject details = m.getAsJsonObject("details");

                    // i'm so so so so so so so so so sorry this has happened to your poor, poor soul
                    if (details.has("d0") && details.get("d0").isJsonPrimitive() && details.has("d0c") && details.get("d0c").isJsonPrimitive())
                        created_move = created_move.d0( details.get("d0").getAsString(), stringToColor( details.get("d0c").getAsString()) );
                    if (details.has("d1") && details.get("d1").isJsonPrimitive() && details.has("d1c") && details.get("d1c").isJsonPrimitive())
                        created_move = created_move.d1( details.get("d1").getAsString(), stringToColor( details.get("d1c").getAsString()) );
                    if (details.has("d2") && details.get("d2").isJsonPrimitive() && details.has("d2c") && details.get("d2c").isJsonPrimitive())
                        created_move = created_move.d2( details.get("d2").getAsString(), stringToColor( details.get("d2c").getAsString()) );
                    if (details.has("d3") && details.get("d3").isJsonPrimitive() && details.has("d3c") && details.get("d3c").isJsonPrimitive())
                        created_move = created_move.d3( details.get("d3").getAsString(), stringToColor( details.get("d3c").getAsString()) );
                    if (details.has("d4") && details.get("d4").isJsonPrimitive() && details.has("d4c") && details.get("d4c").isJsonPrimitive())
                        created_move = created_move.d4( details.get("d4").getAsString(), stringToColor( details.get("d4c").getAsString()) );
                    if (details.has("d5") && details.get("d5").isJsonPrimitive() && details.has("d5c") && details.get("d5c").isJsonPrimitive())
                        created_move = created_move.d5( details.get("d5").getAsString(), stringToColor( details.get("d5c").getAsString()) );
                }

                // TODO: change from builder
                monster = monster.with_move(created_move);

            }
        }

        return monster;
    }

    public MonsterInfo with_move(Move move) {
//        if (num_moves > MAX_MOVES)
//           return this;

        moves.add(move);

        return this;
    }

    public MonsterInfo with_ai(String ai) {
        this.ai_description = ai;
        return this;
    }

    public void print() {
        System.out.println("Monster name: " + name + " (" + id + ") -- " + hp_string);

        for (Move m : moves) {
            System.out.print("Move: " + m.move_name + " | ");
        }
        System.out.println();
    }

    // Getters
    public String getId() { return id; }
    public String getNameAndHP() { return name + "  " +  hp_string; }
    public String getAI() { return ai_description; }
    public String getNotes() { return notes; }

    //public boolean moveExists(int i) { return num_moves > i; }

    public @Nullable
    Move getMove(int i) {
        if (i < moves.size())
            return moves.get(i);
        else
            return null;
    }
}
