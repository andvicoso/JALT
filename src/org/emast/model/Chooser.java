package org.emast.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Anderson
 */
public interface Chooser<T> {

    Set<T> choose(Collection<Map<T, Double>> pValues);
}
