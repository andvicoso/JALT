package org.emast.model.planning.chooser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.Combinator;

/**
 *
 * @author Anderson
 */
public class MinValueChooser<T> implements Chooser<T> {

    private final Combinator<T> combinator;

    public MinValueChooser(Combinator<T> pCombinator) {
        combinator = pCombinator;
    }

    @Override
    public Set<T> choose(Collection<Map<T, Double>> pReps) {
        T ret = null;
        //combine values for propositions from collection
        Map<T, Double> map = combinator.combine(pReps);
        Double min = Collections.min(map.values());

        for (Map.Entry<T, Double> entry : map.entrySet()) {
            T prop = entry.getKey();
            Double d = entry.getValue();

            if (min.equals(d)) {
                ret = prop;
                break;
            }
        }
        return Collections.singleton(ret);
    }
}
