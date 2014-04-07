package org.jalt.model.algorithm.actionchooser;

import java.util.Map;

import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public interface ValuedObjectChooser<T> {

	T choose(Map<T, Double> pValues, State state);
}
