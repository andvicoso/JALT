package org.emast.model.function;

import java.util.Collection;
import org.emast.model.BadReward;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class BadRewardFunction<M extends MDP & ERG & BadReward> implements RewardFunction {

    private final M model;

    public BadRewardFunction(final M pModel) {
        model = pModel;
    }

    @Override
    public double getValue(final State pState, final Action pAction) {
        final Proposition badProp = model.getBadRewardProp();
        final PropositionFunction pf = model.getPropositionFunction();
        final Collection<State> badStates = pf.getStatesWithProposition(badProp);
        //any state that leads to a bad proposition gives a getBadReward()
        final Collection<State> nextStates = model.getTransitionFunction().getFinalStates(
                model.getStates(), pState, pAction);
        for (final State state : nextStates) {
            if (badStates.contains(state)) {
                return model.getBadReward();
            }
        }

        return model.getOtherwiseValue();
    }
}
