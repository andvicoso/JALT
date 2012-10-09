package org.emast.model.planning.rewardcombinator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class MeanRewardCombinator implements RewardCombinator {

    @Override
    public Map<Proposition, Double> combine(final Collection<Map<Proposition, Double>> pReputations) {
        if (pReputations.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        final Map<Proposition, Double> result = new HashMap<Proposition, Double>();
        final Map<Proposition, Integer> count = new HashMap<Proposition, Integer>();
        //find sums and counts
        for (Map<Proposition, Double> map : pReputations) {
            for (Proposition prop : map.keySet()) {
                //count
                Integer c = count.get(prop);
                count.put(prop, (c == null ? 0 : c) + 1);
                //sum
                Double current = map.get(prop);
                Double sum = result.get(prop);
                current = current == null ? 0 : current;
                sum = sum == null ? 0 : sum;

                result.put(prop, current + sum);
            }
        }
        //mean
        for (Proposition prop : result.keySet()) {
            Integer c = count.get(prop);
            Double sum = result.get(prop);

            result.put(prop, sum / c);
        }

        return result;
        //new TreeMap<Proposition, Double>(new ValueComparator(result));
    }
}
