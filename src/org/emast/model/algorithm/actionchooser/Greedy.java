package org.emast.model.algorithm.actionchooser;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * The implmentation returns a random action from those with the highest Q-value
 * 
 * @author andvicoso
 * 
 */
public class Greedy<T> implements ValuedObjectChooser<T> {

	public Greedy() {
	}

	@Override
	public T choose(Map<T, Double> pValues, State pState) {
		// select max
		double max = Collections.max(pValues.values());
		Set<T> maxActions = CollectionsUtils.getKeysForValue(pValues, max);
		// get any of them
		return CollectionsUtils.getRandom(maxActions);
	}

}
