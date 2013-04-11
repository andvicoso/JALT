package org.emast.model.chooser;

import org.emast.model.chooser.base.SingleChooser;
import org.emast.model.chooser.base.MultiChooser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Anderson
 */
public class MaxValueChooser<T> implements SingleChooser<T>, MultiChooser<T> {

    @Override
    public Set<T> choose(Map<T, Double> pValues) {
        Set<T> ret = new HashSet<T>();
        Double min = Collections.min(pValues.values());

        for (Map.Entry<T, Double> entry : pValues.entrySet()) {
            T t = entry.getKey();
            Double d = entry.getValue();

            if (min.equals(d)) {
                ret.add(t);
                break;
            }
        }
        return ret;
    }

    @Override
    public T chooseOne(Map<T, Double> pValues) {
        T ret = null;
        Double min = Collections.min(pValues.values());

        for (Map.Entry<T, Double> entry : pValues.entrySet()) {
            T prop = entry.getKey();
            Double d = entry.getValue();

            if (min.equals(d)) {
                ret = prop;
                break;
            }
        }
        return ret;
    }
}
