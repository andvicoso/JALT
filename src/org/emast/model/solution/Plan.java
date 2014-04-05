package org.emast.model.solution;

import java.util.ArrayList;
import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

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
