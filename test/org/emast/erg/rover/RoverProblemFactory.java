package org.emast.erg.rover;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class RoverProblemFactory extends ProblemFactory {

    private final int rows;
    private final int cols;
    private final int agents;
    private final int numberOfObstacles;

    public RoverProblemFactory(final int pRows, final int pCols,
            final int pAgents, final int pNumberOfObstacles) {
        rows = pRows;
        cols = pCols;
        agents = pAgents;
        numberOfObstacles = pNumberOfObstacles;
    }

    @Override
    public Problem doCreate() {
        //create model
        final RoverModel model = new RoverModel(rows, cols, agents);
        //create proposition function
        final PropositionFunction pf = new PropositionFunction();
        final Set<Proposition> obstacles = RoverModel.getObstacles();
        //spread obstacles all over the grid
        for (int i = 0; i < numberOfObstacles; i++) {
            final Proposition p = getRandom(obstacles);
            pf.add(getRandomEmptyState(model), p);
        }
        //put final goal over the grid
        pf.add(getRandomEmptyState(model), new Proposition("exit"));
        model.setPropositionFunction(pf);
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);
        final Map<Integer, State> map = CollectionsUtils.asIndexMap(initStates);

        return new Problem(model, map);
    }
}
