package org.emast.model.algorithm.planning.rewardcombinator.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.emast.model.algorithm.planning.rewardcombinator.RewardCombinator;
import org.emast.model.propositional.Proposition;
import org.emast.util.comparator.ValueComparator;

/**
 *
 * @author Anderson
 */
public class MeanRewardCombinator implements RewardCombinator {

    @Override
    public Map<Proposition, Double> combine(final Collection<Map<Proposition, Double>> pReputations) {
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

        return new TreeMap<Proposition, Double>(new ValueComparator(result));
    }
}
