package InfoMod;

import java.util.ArrayList;
import java.util.Vector;

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


}
