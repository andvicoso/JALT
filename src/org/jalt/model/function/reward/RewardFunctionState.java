package org.jalt.model.function.reward;

import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.state.State;
import org.jalt.util.DefaultTestProperties;

/**
 * 
 * @author andvicoso
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
				} else if (state.equals(nextState)) {
					table.put(state, action, DefaultTestProperties.BAD_REWARD);
				} else {
					table.put(state, action, getOtherwiseValue());
				}
			}
		}

	}
}
