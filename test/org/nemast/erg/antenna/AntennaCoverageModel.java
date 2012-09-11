package org.nemast.erg.antenna;

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
 * Problem that represents a grid with holes, stones and antennas. The agent must find the path to exit up,
 * avoid the obstacles and keep itself inside an antenna coverage signal.
 *
 * Basic Grid Caption: 0-9: Initial agent x position h: hole s: stone w: water a: antenna c: antenna coverage
 * u: exit up d: exit down X: w a
 *
 * @author anderson
 *
 * ------------ Default Grid ------------ 0 1 2 3 4 0 1 c c X u 1 h h c c c 2 a w c c s 3 c s c a c 4 0 d c c
 */
public class AntennaCoverageModel extends GridMDPModel implements ERG, BadReward {

    public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents,
            final int pAntennaSignalCityBlockRadius) {
        super(pRows, pCols, pAgents);

        AntennaCoverageProblemFactory.createAntennaCoverage(getStates(),
                getPropositionFunction(), new Proposition("antenna"),
                new Proposition("coverage"), pAntennaSignalCityBlockRadius, getPropositions());
    }

    @Override
    public Expression getGoal() {
        return new Expression("up & exit");
    }

    @Override
    public Expression getPreservationGoal() {
        return new Expression("!hole & !stone & coverage");
    }

    @Override
    public Proposition getBadReward() {
        return new Proposition("water");
    }

    @Override
    public double getBadRewardValue() {
        return -30d;
    }

    @Override
    public RewardFunction getRewardFunction() {
        final Proposition water = getBadReward();
        final PropositionFunction pf = getPropositionFunction();
        final Collection<State> badStates = pf.getStatesWithProposition(water);

        return new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                //any state that leads to a water proposition gives an -30 reward
                Collection<State> nextStates = getTransitionFunction().getFinalStates(getStates(),
                        pState, pAction);
                for (State state : nextStates) {
                    if (badStates.contains(state)) {
                        return getBadRewardValue();
                    }
                }

                return -3d;
            }
        };
    }

    @Override
    public PropositionFunction getPropositionFunction() {
        final Proposition hole = new Proposition("hole");
        final Proposition stone = new Proposition("stone");
        final Proposition water = new Proposition("water");
        final Proposition antenna = new Proposition("antenna");
        final Proposition exit = new Proposition("exit");
        final Proposition up = new Proposition("up");
        final Proposition down = new Proposition("down");

        final PropositionFunction pf = new PropositionFunction();
        pf.addGridStatePropositions(2, 4, stone);
        pf.addGridStatePropositions(2, 1, water);
        pf.addGridStatePropositions(0, 3, water);
        pf.addGridStatePropositions(1, 1, hole);
        pf.addGridStatePropositions(1, 0, hole);
        pf.addGridStatePropositions(3, 1, stone);
        pf.addGridStatePropositions(2, 0, antenna);
        pf.addGridStatePropositions(0, 3, antenna);
        pf.addGridStatePropositions(3, 3, antenna);
        pf.addGridStatePropositions(0, 4, exit, up);
        pf.addGridStatePropositions(4, 2, exit, down);

        return pf;
    }

    @Override
    public Set<Proposition> getPropositions() {
        String[] props = {"hole", "stone", "water", "exit", "up", "down", "antenna", "coverage"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }

    @Override
    public void setPreservationGoal(Expression pPreservationGoal) {
    }

    @Override
    public void setGoal(Expression pGoal) {
    }
}
