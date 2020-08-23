package InfoMod.modules.compendium;

import InfoMod.utils.RenderingUtils;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/*
   Utility class for storing data about the monster's moveset and AI. MonsterInfoDatabase loads this information from
   a JSON file included in the JAR at the start of the game. MonsterInfoOverlay will display these details in a nicer
   user-friendly manner. Because the overlay requires some additional layout information built in, the moves are
   stored in a slightly convoluted format.
*/
public class MonsterInfo {
    // A move available in this monster's AI toolkit. The spreadsheet has space for 6 moves total in the monster AI.
    // The d strings are the details of what the move does / the effects it applies. They are spread out so each
    // move interaction can have its own font color; there are (hopefully) enough to provide ample space for longer
    // effects. (Not all detailed strings may contain content; and they may not be contiguous, e.g. d0 and d4 may be
    // the only ones if d0 has a lot of text -- this is up to the JSON creator to make a judgement to avoid overlap)
    //
    // TODO: the Champ and the Guardian both may require special handling as they go over the 6 moves limit with 7 each
    //   I did not account for 2-stage bosses while considering the 6 move maximum based on the spreadsheet. improvements
    //   are probably necessary in the future
    public static class Move {
        public String move_name;
        public String d0, d1, d2, d3, d4, d5;
        public Color c0, c1, c2, c3, c4, c5;

        public Move(String name) {
            move_name = name;
            c0 = c1 = c2 = c3 = c4 = c5 = RenderingUtils.OJB_DARK_GRAY_COLOR;
        }

        // Set the detail/color string in a particular slot (param "which" specifies the slot location)
        // Better ways to do this definitely exist, but this works well enough
        public void setDetail(String which, String d, String c) {
            if (which == "d0") {
                this.d0 = d;
                this.c0 = stringToColor(c);
            }
            else if (which == "d1") {
                this.d1 = d;
                this.c1 = stringToColor(c);
            }
            else if (which == "d2") {
                this.d2 = d;
                this.c2 = stringToColor(c);
            }
            else if (which == "d3") {
                this.d3 = d;
                this.c3 = stringToColor(c);
            }
            else if (which == "d4") {
                this.d4 = d;
                this.c4 = stringToColor(c);
            }
            else if (which == "d5") {
                this.d5 = d;
                this.c5 = stringToColor(c);
            }
        }
    }

    // Basic details about this monster
    private String id;
    private String name;
    private String hp_string;

    // Moves available
    ArrayList<Move> moves = new ArrayList<>();

    // Describes the AI behavior / when to take each of the moves
    private String ai_description;

    // Describes additional details about the monster (e.g. does it start with innate artifact / powers? etc.)
    private String notes;

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
                System.out.println("OJB: invalid color at some point (bug in monsters.JSON!)");
                System.out.println("OJB: invalid color is: " + s);
                return RenderingUtils.OJB_DARK_GRAY_COLOR;
        }
    }

    // Checks to see if a detail exists in the given "dx/dxc" detail slot (x is the slot number, e.g. d0/d0c)
    // If it does, put it into the given move in the proper place
    private static void updateMoveWithDetail(String dx, String dxc, JsonObject details, Move created_move) {
        boolean detailStringOK = (details.has(dx) && details.get(dx).isJsonPrimitive());
        boolean detailColorOK = (details.has(dxc) && details.get(dxc).isJsonPrimitive());

        if (detailStringOK && detailColorOK) {
            created_move.setDetail(dx, details.get(dx).getAsString(), details.get(dxc).getAsString());
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

                // Details are optional and can fit in 6 different slots
                if (m.has("details") && m.get("details").isJsonObject()) {
                    JsonObject details = m.getAsJsonObject("details");

                    updateMoveWithDetail("d0", "d0c", details, created_move);
                    updateMoveWithDetail("d1", "d1c", details, created_move);
                    updateMoveWithDetail("d2", "d2c", details, created_move);
                    updateMoveWithDetail("d3", "d3c", details, created_move);
                    updateMoveWithDetail("d4", "d4c", details, created_move);
                    updateMoveWithDetail("d5", "d5c", details, created_move);
                }

                // TODO: change from builder
                monster.moves.add(created_move);
            }
        }

        return monster;
    }

    // DEBUG
    public void print() {
        System.out.println("Monster name: " + name + " (" + id + ") -- " + hp_string);

        System.out.print("\t");
        for (Move m : moves) {
            System.out.print(m.move_name + ", ");
        }
        System.out.println();
    }

    // Getters
    public String getId() { return id; }
    public String getNameAndHP() { return name + "  " +  hp_string; }
    public String getAI() { return ai_description; }
    public String getNotes() { return notes; }

    public @Nullable
    Move getMove(int i) {
        if (i < moves.size())
            return moves.get(i);
        else
            return null;
    }
}
