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
public class MeanValueCombinator implements Combinator<Object> {

    @Override
    public Map<Object, Double> combine(final Collection<Map<Object, Double>> pValues) {
        if (pValues.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        final Map<Object, Double> result = new HashMap<Object, Double>();
        final Map<Object, Integer> count = new HashMap<Object, Integer>();
        //find sums and counts
        for (Map<Object, Double> map : pValues) {
            for (Object prop : map.keySet()) {
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
        for (Object prop : result.keySet()) {
            Integer c = count.get(prop);
            Double sum = result.get(prop);

            result.put(prop, sum / c);
        }

        return result;
    }
}
