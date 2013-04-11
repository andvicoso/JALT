package org.emast.model.chooser.base;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Anderson
 */
public interface MultiChooser<T> {

    Set<T> choose(Map<T, Double> pValues);
}
