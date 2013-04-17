package org.emast.model.chooser;

import org.emast.model.chooser.base.SingleChooser;
import org.emast.model.chooser.base.MultiChooser;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class MinValueChooser<T> implements SingleChooser<T>, MultiChooser<T> {

    @Override
    public Set<T> choose(Map<T, Double> pValues) {
        Double min = Collections.min(pValues.values());
        return CollectionsUtils.getKeysForValue(pValues, min);
    }

    @Override
    public T chooseOne(Map<T, Double> pValues) {
        return choose(pValues).iterator().next();
    }
}
