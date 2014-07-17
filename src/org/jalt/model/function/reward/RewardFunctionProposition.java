package org.jalt.model.function.reward;

import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.DefaultTestProperties;

/**
 * MUST BE ADDED TO THE MODEL AFTER PROPOSITION FUNCTION!
 * 
 * @author andvicoso
 */

public class RewardFunctionProposition<M extends ERG> extends DefaultRewardFunction<M, Proposition> {

	public RewardFunctionProposition(M pModel, Map<Proposition, Double> pRewardValues,
			double pOtherwiseValue) {
		super(pModel, pRewardValues, pOtherwiseValue);

		PropositionFunction pf = getModel().getPropositionFunction();
		TransitionFunction tf = getModel().getTransitionFunction();

		for (State state : pModel.getStates()) {
			for (Action action : pModel.getActions()) {
				State nextState = tf.getNextState(getModel().getStates(), state, action);
				Set<Proposition> props = pf.getPropositionsForState(nextState);
				Double value = getOtherwiseValue();

				if (state.equals(nextState)) {
					// avoid to go to outside the grid (value iteration fix)
					value = DefaultTestProperties.BAD_REWARD;
				} else if (props != null) {
					for (Proposition condition : props) {
						if (pRewardValues.containsKey(condition)) {
							value = pRewardValues.get(condition);
							break;
						}
					}
				}

				table.put(state, action, value);
			}
		}
	}
}
