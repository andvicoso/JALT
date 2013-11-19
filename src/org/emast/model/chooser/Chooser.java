package org.emast.model.chooser;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Anderson
 */
public interface Chooser<T> {

	Set<T> choose(Map<T, Double> pValues);

	T chooseOne(Map<T, Double> pValues);
}
