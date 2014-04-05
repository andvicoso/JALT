package org.emast.model.function.transition;

import java.util.Collection;

import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class TableTransitionFunction extends TransitionFunction {
	// private static final String GLUE = ".";
	private final Double[][][] values;

	public TableTransitionFunction(Collection<State> states, Collection<Action> actions,
			TransitionFunction transitionFunction) {
		values = new Double[states.size()][actions.size()][states.size()];

		for (State state : states) {
			for (Action action : actions) {
				for (State finalState : states) {
					double value = transitionFunction.getValue(state, finalState, action);
					int row = state.hashCode() % states.size();
					int col = action.hashCode() % actions.size();
					int breadth = finalState.hashCode() % states.size();
					values[row][col][breadth] = value;
				}
			}
		}
	}

	@Override
	public double getValue(State state, State finalState, Action action) {
		int row = state.hashCode() % values.length;
		int col = action.hashCode() % values[0].length;
		int breadth = finalState.hashCode() % values[0][0].length;
		Double value = values[row][col][breadth];
		return value == null ? 0 : value;
	}
}
