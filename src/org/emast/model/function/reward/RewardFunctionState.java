package org.emast.model.function.reward;

import java.util.Collection;
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
    }

    @Override
    public double getValue(final State pState, final Action pAction) {
        for (State cond : getRewards().keySet()) {
            //any state that leads to a bad proposition gives a getBadReward()
            final Collection<State> nextStates = getModel().getTransitionFunction().getFinalStates(
                    getModel().getStates(), pState, pAction);
            if (nextStates.contains(cond)) {
                return getRewards().get(cond);
            }
        }

        return getOtherwiseValue();
    }
}
