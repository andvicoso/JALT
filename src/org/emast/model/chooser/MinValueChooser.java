package org.emast.model.chooser;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.emast.util.CollectionsUtils;

/**
 * 
 * @author andvicoso
 */
public class MinValueChooser<T> implements Chooser<T> {

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
