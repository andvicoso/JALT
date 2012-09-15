package org.emast.model.function;

import java.util.Collection;
import org.emast.model.BadReward;
import org.emast.model.BadRewarder;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class BadRewardFunction<M extends ERG & BadRewarder> implements RewardFunction {

    private final M model;

    public BadRewardFunction(final M pModel) {
        model = pModel;
    }

    @Override
    public double getValue(final State pState, final Action pAction) {
        for (BadReward badReward : model.getBadRewards()) {
            final Proposition badProp = badReward.getBadRewardProp();
            final PropositionFunction pf = model.getPropositionFunction();
            final Collection<State> badStates = pf.getStatesWithProposition(badProp);
            //any state that leads to a bad proposition gives a getBadReward()
            final Collection<State> nextStates = model.getTransitionFunction().getFinalStates(
                    model.getStates(), pState, pAction);
            for (final State state : nextStates) {
                if (badStates.contains(state)) {
                    return badReward.getBadReward();
                }
            }
        }

        return model.getOtherwiseValue();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(model.getBadRewards());
        sb.append(", otherwise=").append(model.getOtherwiseValue());
        return sb.toString();
    }
}
