package org.emast.model.function.reward;

import java.util.Collection;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public class RewardFunctionProposition<M extends ERG> extends DefaultRewardFunction<M, Proposition> {

	public RewardFunctionProposition(M pModel, Map<Proposition, Double> pRewardValues,
			double pOtherwiseValue) {
		super(pModel, pRewardValues, pOtherwiseValue);
	}

	@Override
	// TODO: melhorar
	public double getValue(final State pState, final Action pAction) {
		final PropositionFunction pf = getModel().getPropositionFunction();
		final TransitionFunction tf = getModel().getTransitionFunction();
		for (Proposition condition : getRewards().keySet()) {
			final Collection<State> rewardStates = pf.getStatesWithProposition(condition);
			// any state that leads to a bad proposition gives a getBadReward()
			final State nextState = tf.getNextState(getModel().getStates(), pState, pAction);
			if (rewardStates.contains(nextState)) {
				return getRewards().get(condition);
			}
		}

//		final State nextState = tf.getNextState(getModel().getStates(), pState, pAction);
//		if (pf.getExpressionForState(nextState).contains(getModel().getGoal()))
//			return 50;

		return getOtherwiseValue();
	}
}
