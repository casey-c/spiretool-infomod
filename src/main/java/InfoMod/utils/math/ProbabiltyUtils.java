package InfoMod.utils.math;

import InfoMod.InfoMod;

import java.util.ArrayList;
import java.util.Vector;

/*
   This class helps compute the rare card chance probability. It's ported over from SpireTool's c++ and not documented.
 */
public class ProbabiltyUtils {
    public enum CARD_RARITY {
        CARD_COMMON,
        CARD_UNCOMMON,
        CARD_RARE
    }

    private static Vector<CARD_RARITY> recursiveTripleGen(int len) {
        Vector<CARD_RARITY> result = new Vector<>();

        if (len <= 1) {
            result.add(CARD_RARITY.CARD_COMMON);
            result.add(CARD_RARITY.CARD_UNCOMMON);
            result.add(CARD_RARITY.CARD_RARE);
            return result;
        }
        else {
            Vector<CARD_RARITY> lower = recursiveTripleGen(len - 1);

            int stride = len - 1;
            for (int i = 0; i < lower.size(); i += stride) {
                Vector<CARD_RARITY> strided = new Vector<>();

                for (int j = 0; j < stride; ++j)
                    strided.add(lower.get(i + j));

                result.add(CARD_RARITY.CARD_COMMON);
                result.addAll(strided);

                result.add(CARD_RARITY.CARD_UNCOMMON);
                result.addAll(strided);

                result.add(CARD_RARITY.CARD_RARE);
                result.addAll(strided);
            }

            return result;
        }
    }

    private static double calcProbSequence(ArrayList<CARD_RARITY> sequence, int cbr, int rareChance) {
        double result = 1.0;

        int uncommonChance = 37;
        int combined_chance = rareChance + uncommonChance;

        for (CARD_RARITY r : sequence) {
            double pr_rare = ((double)rareChance - (double)cbr) / 100.0;
            if (pr_rare < 0.0) pr_rare = 0.0;

            double pr_uncommon = (((double)combined_chance - (double)cbr) / 100.0) - pr_rare;
            double pr_common = 1.0 - (pr_rare + pr_uncommon);

            switch(r) {
                case CARD_COMMON:
                    result *= pr_common;
                    cbr -= 1;
                    if (cbr < -40)
                        cbr = -40;
                    break;
                case CARD_UNCOMMON:
                    result *= pr_uncommon;
                    break;
                case CARD_RARE:
                    result *= pr_rare;
                    cbr = 5;
                    break;
            }
        }

        return result;
    }

    /*
       exponential brute force algorithm. generate all possibilities (e.g. common, common, common or common, rare,
       uncommon), then compute the probabilities of each, looking for only the sequences that contain the target rarity.

       this is expensive as hell, but easier to reason about than the pure probability approach (which is over my head)
       luckily the exponential run time doesn't matter when numCards <= 8 at maximum, with the all vector containing
       3^8 = ~6500 entries in that case. we try to not run this every frame obviously, but only when the rare chance
       changes (e.g. upon generating a card)
     */
    public static double computeCardProb(int cbr, int numCards, int rareChance, CARD_RARITY rarity) {
        double final_prob = 0.0;

        Vector<CARD_RARITY> all = recursiveTripleGen(numCards);

        for (int i = 0; i < all.size(); i += numCards) {
            // Get the current sequence using a stride of length numCards
            ArrayList<CARD_RARITY> sequence = new ArrayList<>();

            for (int j = 0; j < numCards; ++j)
                sequence.add(all.get(i + j));

            // Verify that this sequence contains the target rarity type
            if (!sequence.contains(rarity))
                continue;

            // Calculate the probability of this sequence occuring
            final_prob += calcProbSequence(sequence, cbr, rareChance);
        }

        return final_prob;
    }

    ////////////////////////////////////////////////////////////////////////////////

    // TODO: Future event chances (WIP)

    // Note: assumes at least 1 shrine exists (pretty much a certainty)
    // Takes: initial fight chance, treasure chance, shop chance, and number of events left in the pool
    public static float[] chanceOfSeeingEventAfter2(float f, float t, float s, int x) {
        float[] output = new float[2];
        output[0] = 0.0f;
        output[1] = 0.0f;

        // Not enough events in the pool
        if (x < 1)
            return output;

        // Cast int x to float for remainder
        final float fx = (float)x;
        final float pc = 1.0f / fx; // chance to pull specific event in the event pool of size x

        // Ramp up constants (TODO: pull from AbstractDungeon?)
        final float fr = 0.1f;  // fight ramp
        final float tr = 0.02f; // treasure ramp
        final float sr = 0.03f; // shop ramp

        // Ramp resets (TODO: pull from AbstractDungeon?)
        // note: by default, these reset values are the same as the ramps
        final float f0 = fr;
        final float t0 = tr;
        final float s0 = sr;

        // Chance to see any event/shrine
        final float E = 1.0f - t - f - s;

        // Roll event: 75% of the time it is a regular event; 25% of the time it is a shrine
        // Regular events get pulled from pool of size x at the start, shrines from a shrine pool (who cares)
        final float evtShrineRatio = 0.75f;
        final float shrineEvtRatio = 0.25f;

        /*----------------------+
        |  First question mark  |
        +----------------------*/

        // A1 = chance we got our desired event on the first ? floor
        final float A1 = E * evtShrineRatio * (1.0f / fx);

        // A2 = chance we saw a different event on the first ? floor
        final float A2 = E * evtShrineRatio * ((fx - 1.0f) / fx);

        // A3 = chance we saw a shrine on the first ? floor
        final float A3 = E * shrineEvtRatio;

        // A4 = chance we saw a fight on the first ? floor
        final float A4 = f;

        // A5 = chance we saw a treasure on the first ? floor
        final float A5 = t;

        // A6 = chance we saw a shop on the first ? floor
        final float A6 = s;

        /*-----------------------+
        |  Second question mark  |
        +-----------------------*/

        // Ramp-ups
        // all ramp up, fight reset others ramp up, treasure reset others ramp up, etc.
        final float fullRamp = 1.0f - f - fr - t - tr - s - sr;
        final float fightRamp = 1.0f - f0 - t - tr - s - sr;
        final float treasureRamp = 1.0f - f - fr - t0 - s - sr;
        final float shopRamp = 1.0f - f - fr - t - tr - s0;

        // B1A1 = chance we've seen our desired event after 2 floors given that we've seen it on the first
        final float B1A1 = 1.0f; // duh

        // B1A2 = chance we see our event on the second ? floor after seeing a diff. event on the first
        final float B1A2 = (x > 1) ? fullRamp * evtShrineRatio * (1.0f / (fx - 1.0f)) : 1.0f;

        // B1A3 = chance we see our evt on second ? after seeing a shrine on first
        final float B1A3 = fullRamp * evtShrineRatio * pc;

        // B1A4 = chance we see evt on second ? after seeing a fight on first
        final float B1A4 = fightRamp * evtShrineRatio * pc;

        // B1A5 = chance we see evt on second ? after seeing a treasure on first
        final float B1A5 = treasureRamp * evtShrineRatio * pc;

        // B1A6 = chance we see evt on second ? after seeing a shop on first
        final float B1A6 = shopRamp * evtShrineRatio * pc;

        // Final chance to see the desired event after at least two floors:
        final float prAfter2 = A1 + (B1A2 * A2) + (B1A3 * A3) + (B1A4 * A4) + (B1A5 * A5) + (B1A6 * A6);

        output[0] = A1;
        output[1] = prAfter2;

        return output;
    }


}
