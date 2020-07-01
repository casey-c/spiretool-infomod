package InfoMod;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
  Slay The Relics (Twitch extension) integration. (WIP)

 NOTE:  I don't like the idea of clearing / rebuilding the hitboxes on every render as suggested (seems like there are
   a number of better solutions to try). Right now, I rebuild on a change (but only allow a single at a time). Since we
   have multiple items with hitboxes that can update at a lot of different times, we'll need to expand this
   implementation a bit further to handle these async updates.
 */
@SpireInitializer
public class SlayTheRelicsIntegration {
    public static ArrayList<Hitbox> slayTheRelicsHitboxes = new ArrayList<>();
    public static ArrayList<ArrayList<PowerTip>> slayTheRelicsPowerTips = new ArrayList<>();

    private static HashMap<String, Map.Entry<Hitbox, ArrayList<PowerTip>>> tipMap = new HashMap<>();

    public static void initialize() {}

    // DEBUG
    public static void print() {
        System.out.println( "OJB: SlayTheRelics Integration -- map size " + tipMap.size() + " | hitbox list size " + slayTheRelicsHitboxes.size() + " | powertips " + slayTheRelicsPowerTips.size() );

        for (ArrayList<PowerTip> list : slayTheRelicsPowerTips) {
            for (PowerTip tip : list) {
                System.out.println("tip: " + tip.header + " | " + tip.body);
            }
        }
    }

    // TODO: call this on character change or return to main menu?
    public static void reset() {
        tipMap.clear();
        clear();
    }

    public static void update(String id, Hitbox hb, ArrayList<PowerTip> tips) {
        // Update the hash map
        Map.Entry<Hitbox,ArrayList<PowerTip>> entry = new AbstractMap.SimpleEntry<>(hb, tips);
        tipMap.put(id, entry);

        // Update the lists
        clear();

        for (Map.Entry<Hitbox, ArrayList<PowerTip>> map_entry : tipMap.values()) {
            Hitbox map_hb = map_entry.getKey();
            ArrayList<PowerTip> map_tip = map_entry.getValue();
            add(map_hb, map_tip);
        }
    }

    private static void clear() {
        slayTheRelicsHitboxes.clear();
        slayTheRelicsPowerTips.clear();
    }

    private static void add(Hitbox hb, ArrayList<PowerTip> tips) {
        slayTheRelicsHitboxes.add(hb);
        slayTheRelicsPowerTips.add(tips);
    }
}
