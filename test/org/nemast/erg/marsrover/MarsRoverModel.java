package org.nemast.erg.marsrover;

import java.util.Collection;
import java.util.Set;
import org.emast.model.BadReward;
import org.emast.model.action.Action;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.RewardFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.GridMDPModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class MarsRoverModel extends GridMDPModel implements ERG, BadReward {

    public MarsRoverModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols, pAgents);
    }

    @Override
    public RewardFunction getRewardFunction() {
        final Proposition water = getBadReward();
        final PropositionFunction pf = getPropositionFunction();
        final Collection<State> badStates = pf.getStatesWithProposition(water);

        final RewardFunction rf = new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                Collection<State> nextStates = getTransitionFunction().getFinalStates(getStates(),
                        pState, pAction);
                for (State state : nextStates) {
                    if (badStates.contains(state)) {
                        return getBadRewardValue();
                    }
                }
                return -1;
            }
        };

        return rf;

        //pModel.getStates(0, 0, 0, 3, 3, 0)
    }

    @Override
    public PropositionFunction getPropositionFunction() {
        final PropositionFunction pf = new PropositionFunction();
        pf.addGridStatePropositions(1, 0, "water");
        pf.addGridStatePropositions(2, 1, "hole");
        pf.addGridStatePropositions(3, 1, "water");
        pf.addGridStatePropositions(1, 2, "hole");
        pf.addGridStatePropositions(2, 2, "stone");
        pf.addGridStatePropositions(1, 1, "stone");
        pf.addGridStatePropositions(3, 3, "exit");

        return pf;
    }

    @Override
    public Set<Proposition> getPropositions() {
        String[] props = {"hole", "stone", "water", "exit"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }

    @Override
    public Expression getPreservationGoal() {
        return new Expression("!hole & !stone");
    }

    @Override
    public void setPreservationGoal(Expression pPreservationGoal) {
    }

    @Override
    public Expression getGoal() {
        return new Expression("exit");
    }

    @Override
    public void setGoal(Expression pGoal) {
    }

    @Override
    public double getBadRewardValue() {
        return -20d;
    }

    @Override
    public Proposition getBadReward() {
        return new Proposition("water");
    }
}
