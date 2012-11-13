package org.emast.model;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Anderson
 */
public interface Combinator<T> {

    Map<T, Double> combine(final Collection<Map<T, Double>> pValues);
}
