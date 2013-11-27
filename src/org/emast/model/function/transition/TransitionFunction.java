package org.emast.model.function.transition;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.actionchooser.Greedy;
import org.emast.model.algorithm.actionchooser.ValuedObjectChooser;
import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public abstract class TransitionFunction implements Serializable {
	private ValuedObjectChooser<State> chooser = new Greedy<>();

	public abstract double getValue(final State pState, final State pFinalState,
			final Action pAction);

	public Map<Action, Double> getActionValues(final Collection<Action> pModelActions,
			final State pState) {
		Map<Action, Double> map = new HashMap<Action, Double>();

		for (final Action action : pModelActions) {
			final double value = getValue(pState, State.ANY, action);
			if (value > 0) {
				map.put(action, value);
			}
		}
		return map;
	}

	public Collection<Action> getActionsFrom(final Collection<Action> pModelActions,
			final State pState) {
		return getActionValues(pModelActions, pState).keySet();
	}

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

	public Map<State, Double> getStatesValuesThatReach(final Collection<State> pModelStates,
			final State pState, final Action pActions) {
		final Map<State, Double> map = new HashMap<State, Double>();
		for (final State state : pModelStates) {
			final double value = getValue(state, pState, pActions);
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
		State ret = null;
		Map<State, Double> map = getReachableStatesValues(pModelStates, pState, pAction);
		if (map != null && !map.isEmpty()) {
			ret = chooser.choose(map, pState);
		}
		return ret;
	}

}
