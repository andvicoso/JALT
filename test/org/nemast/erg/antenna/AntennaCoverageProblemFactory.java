package org.nemast.erg.antenna;

import java.util.*;
import org.emast.model.function.PropositionFunction;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import org.emast.util.GridUtils;

/**
 *
 * @author And
 */
public class AntennaCoverageProblemFactory extends ProblemFactory {

    public Problem createProblem(
            final int pRows, final int pCols, final int pAgents,
            final int pNumberOfObstacles, final int pNumberOfAntennas,
            final int pAntennaSignalCityBlockRadius) {

        final AntennaCoverageModel model = new AntennaCoverageModel(pRows, pCols, pAgents,
                pAntennaSignalCityBlockRadius);

        final Proposition hole = new Proposition("hole");
        final Proposition stone = new Proposition("stone");
        final Proposition water = new Proposition("water");
        final Proposition antenna = new Proposition("antenna");
        final Proposition coverage = new Proposition("coverage");
        final Proposition exit = new Proposition("exit");
        final Proposition up = new Proposition("up");
        final Proposition down = new Proposition("down");

        final List<Proposition> obstacles = Arrays.asList(hole, stone, water);

        final PropositionFunction pf = new PropositionFunction();
        //spread obstacles over the grid
        for (int i = 0; i < pNumberOfObstacles; i++) {
            pf.add(getRandomEmptyState(model), getRandom(obstacles));
        }
        //distribute antennas over the grid
        for (int i = 0; i < pNumberOfAntennas; i++) {
            pf.add(getRandomEmptyState(model), antenna);
        }
        model.setPropositionFunction(pf);

        createAntennaCoverage(model.getStates(), pf, antenna, coverage, pAntennaSignalCityBlockRadius,
                model.getPropositions());

        final Set<State> sts = pf.getStatesWithProposition(coverage);
        //put true(up) and fake(down) goals over the grid
        pf.add(getRandom(sts), up, exit);
        pf.add(getRandom(sts), down, exit);
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, pAgents);

        return new Problem(model, CollectionsUtils.asMap(initStates));
    }

    public State getRandomCoverageState(final AntennaCoverageModel model, final List<State> pInitialStates) {
        final List<State> coverage = new ArrayList<State>();
        final Proposition cov = new Proposition("coverage");
        final Set<State> sts = model.getPropositionFunction().getStatesWithProposition(cov);

        for (final State state : sts) {
            final Collection<Proposition> props = model.getPropositionFunction().getPropositionsForState(state);
            if (props.size() == 1 && !pInitialStates.contains(state)) {
                coverage.add(state);
            }
        }

        return getRandom(coverage);
    }

    public Problem createProblem(final int pRows,
            final int pCols, final int pAgents,
            final int pNumberOfObstacles) {
        return createProblem(pRows, pCols, pAgents, pNumberOfObstacles, 2, 1);
    }

    /**
     * Create antennas' coverages radius around them.
     *
     * @param pf
     * @param states
     * @param antenna
     * @param coverage
     * @param pAntennaSignalRadius
     */
    public static void createAntennaCoverage(Collection<State> pModelStates,
            PropositionFunction pf, Proposition antenna, Proposition coverage,
            int pAntennaSignalRadius, Set<Proposition> pProps) {
        //create antennas' coverages
        final Collection<State> antennaStates = pf.getStatesWithProposition(antenna);
        for (final State state : pModelStates) {
            for (final State stateAntenna : antennaStates) {
                if (GridUtils.getCityBlockDistance(state, stateAntenna) <= pAntennaSignalRadius) {
                    pf.add(state, coverage);
                }
            }
        }
    }
}
