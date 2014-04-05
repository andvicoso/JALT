package org.emast.model.function.transition;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.actionchooser.RandomChooser;
import org.emast.model.algorithm.actionchooser.ValuedObjectChooser;
import org.emast.model.state.State;

/**
 * 
 * @author andvicoso
 */
public abstract class TransitionFunction implements Serializable {
	private ValuedObjectChooser<State> chooser = new RandomChooser<>();

	public abstract double getValue(final State pState, final State pFinalState,
			final Action pAction);

	public Map<State, Double> getReachableStatesValues(final Collection<State> pModelStates,
			final State pState, final Action pAction) {
		final Map<State, Double> map = new HashMap<State, Double>();
		for (final State state : pModelStates) {
			final double value = getValue(pState, state, pAction);
			if (value > 0) {
				map.put(state, value);
			}
		}
		return map;
	}

	public Set<State> getReachableStates(final Collection<State> pModelStates, final State pState,
			final Action pAction) {
		return getReachableStatesValues(pModelStates, pState, pAction).keySet();
	}

	public State getNextState(final Collection<State> pModelStates, final State pState,
			final Action pAction) {
		State ret = pState;
		Map<State, Double> map = getReachableStatesValues(pModelStates, pState, pAction);
		if (map != null && !map.isEmpty()) {
			ret = chooser.choose(map, pState);
		}
		return ret;
	}

	public void setChooser(ValuedObjectChooser<State> chooser) {
		this.chooser = chooser;
	}

	public ValuedObjectChooser<State> getChooser() {
		return chooser;
	}

}
