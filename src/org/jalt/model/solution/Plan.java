package org.jalt.model.solution;

import java.util.ArrayList;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class Plan extends ArrayList<Action> {

	public Double getTotalValue(MDP pModel, State pInitialState) {
		double value = 0;
		State state = pInitialState;
		for (final Action action : this) {
			value += pModel.getRewardFunction().getValue(state, action);
			State nextState = pModel.getTransitionFunction().getNextState(pModel.getStates(),
					state, action);
			state = nextState;
		}

		return value;
	}
}
