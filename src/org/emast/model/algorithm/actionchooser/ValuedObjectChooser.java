package org.emast.model.algorithm.actionchooser;

import java.util.Map;

import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public interface ValuedObjectChooser<T> {

	T choose(Map<T, Double> pValues, State state);
}
