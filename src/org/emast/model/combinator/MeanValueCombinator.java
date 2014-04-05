package org.emast.model.combinator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.combinator.Combinator;

/**
 *
 * @author andvicoso
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
            for (T key : map.keySet()) {
                //count
                Integer c = count.get(key);
                count.put(key, (c == null ? 0 : c) + 1);
                //sum
                Double current = map.get(key);
                Double sum = result.get(key);
                current = current == null ? 0 : current;
                sum = sum == null ? 0 : sum;

                result.put(key, current + sum);
            }
        }
        //mean
        for (T key : result.keySet()) {
            Integer c = count.get(key);
            Double sum = result.get(key);

            result.put(key, sum / c);
        }

        return result;
    }
}
