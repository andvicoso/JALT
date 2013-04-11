package org.emast.model.planning.rewardcombinator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.Combinator;

/**
 *
 * @author Anderson
 */
public class MeanValueCombinator<T> implements Combinator<T> {

    @Override
    public Map<T, Double> combine(final Collection<Map<T, Double>> pValues) {
        if (pValues.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        final Map<T, Double> result = new HashMap<T, Double>();
        final Map<T, Integer> count = new HashMap<T, Integer>();
        //find sums and counts
        for (Map<T, Double> map : pValues) {
            for (T prop : map.keySet()) {
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
        for (T prop : result.keySet()) {
            Integer c = count.get(prop);
            Double sum = result.get(prop);

            result.put(prop, sum / c);
        }

        return result;
    }
}
