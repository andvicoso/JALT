package org.emast.model.chooser;

import org.emast.model.chooser.base.MultiChooser;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Anderson
 */
public class ThresholdChooser<T> implements MultiChooser<T> {

    private final double threshold;
    private final boolean lower;

    public ThresholdChooser(double pThreshold, boolean pLower) {
        threshold = pThreshold;
        lower = pLower;
    }

    @Override
    public Set<T> choose(Map<T, Double> pValues) {
        Set<T> set = new HashSet<T>();
        for (T t : pValues.keySet()) {
            if (lower && pValues.get(t) <= threshold) {
                set.add(t);
            } else if (!lower && pValues.get(t) >= threshold) {
                set.add(t);
            }
        }
        return set;
    }
}
