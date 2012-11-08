package org.emast.erg.treasurehunter;

import java.util.Arrays;
import java.util.List;
import org.emast.model.function.PropositionFunction;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author And
 */
public class TreasureHunterProblemFactory
        extends ProblemFactory {

    private final int rows;
    private final int cols;
    private final int agents;
    private final int numberOfObstacles;

    public TreasureHunterProblemFactory(final int pRows, final int pCols, final int pAgents,
            final int pNumberOfObstacles) {
        rows = pRows;
        cols = pCols;
        agents = pAgents;
        numberOfObstacles = pNumberOfObstacles;
    }

    @Override
    protected Problem doCreate() {
        final TreasureHunterModel model = new TreasureHunterModel(rows, cols, agents);

        final Proposition hole = new Proposition("hole");
        final Proposition wall = new Proposition("wall");
        final Proposition treasure = new Proposition("treasure");

        final PropositionFunction pf = new PropositionFunction();
        final List<Proposition> props = Arrays.asList(wall, hole);
        //spread obstacles over the grid
        for (int i = 0; i < numberOfObstacles; i++) {
            final Proposition p = CollectionsUtils.getRandom(props);
            pf.add(getRandomEmptyState(model), p);
        }
        //spread goal over the grid
        pf.add(getRandomEmptyState(model), treasure);
        model.setPropositionFunction(pf);
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);

        return new Problem(model, CollectionsUtils.asIndexMap(initStates));
    }
}
