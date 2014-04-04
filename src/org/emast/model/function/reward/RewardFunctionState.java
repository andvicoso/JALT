package org.emast.model.function.reward;

import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public class RewardFunctionState<M extends MDP> extends DefaultRewardFunction<M, State> {

	public RewardFunctionState(M pModel, Map<State, Double> pRewardValues, double pOtherwiseValue) {
		super(pModel, pRewardValues, pOtherwiseValue);

		for (State state : pModel.getStates()) {
			for (Action action : pModel.getActions()) {
				State nextState = getModel().getTransitionFunction().getNextState(
						getModel().getStates(), state, action);
				if (pRewardValues.containsKey(nextState)) {
					table.put(state, action, pRewardValues.get(nextState));
				}
			}
		}

	}

	@Override
	public double getValue(final State pState, final Action pAction) {
		Double value = table.get(pState, pAction);
		return value != null ? value : getOtherwiseValue();
	}
}
