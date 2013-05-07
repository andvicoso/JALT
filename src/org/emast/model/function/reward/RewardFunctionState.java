package org.emast.model.function.reward;

import java.util.Map;
import java.util.Set;
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
        Set<State> rewardStates = getRewards().keySet();
        //any state that leads to a bad state gives a BadReward
        State nextState = getModel().getTransitionFunction().getBestReachableState(
                getModel().getStates(), pState, pAction);
        if (rewardStates.contains(nextState) && getRewards().containsKey(nextState)) {
            return getRewards().get(nextState);
        }

        return getOtherwiseValue();
    }
}
