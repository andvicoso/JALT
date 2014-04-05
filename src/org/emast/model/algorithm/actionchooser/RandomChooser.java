package org.emast.model.algorithm.actionchooser;

import java.util.Map;

import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * 
 * @author andvicoso
 */
public class RandomChooser<T> implements ValuedObjectChooser<T> {

	@Override
	public T choose(Map<T, Double> pValues, State pState) {
		return CollectionsUtils.draw(pValues);
	}
}
