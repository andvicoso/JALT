package org.jalt.model.function.reward;

import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;

/**
 * MUST BE ADDED TO THE MODEL AFTER PROPOSITION FUNCTION! 
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
				if (props != null)
					for (Proposition condition : props) {
						if (pRewardValues.containsKey(condition)) {
							table.put(state, action, pRewardValues.get(condition));
							break;
						}
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
