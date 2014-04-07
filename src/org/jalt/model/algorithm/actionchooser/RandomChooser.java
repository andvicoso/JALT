package org.jalt.model.algorithm.actionchooser;

import java.util.Map;

import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

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
