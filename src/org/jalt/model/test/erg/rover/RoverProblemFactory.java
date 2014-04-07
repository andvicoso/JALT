package org.jalt.model.test.erg.rover;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jalt.model.converter.ToRL;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

import static org.jalt.util.DefaultTestProperties.*;
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

    public static ProblemFactory createDefaultFactory() {
        int rows = 5;
        int cols = 5;
        int size = rows * cols;
        int obstacles = (int) Math.ceil(0.3 * size);
        int agents = (int) Math.ceil(0.15 * size);

        return new RoverProblemFactory(rows, cols, agents, obstacles);
    }

    @Override
    public Problem<ERG> doCreate() {
        //create model
        final RoverModel model = new RoverModel(rows, cols, agents);
        //create proposition function
        final PropositionFunction pf = new PropositionFunction();
        final Set<Proposition> obstacles = RoverModel.getObstacles();
        //spread obstacles all over the grid
        for (int i = 0; i < numberOfObstacles; i++) {
            final Proposition p = CollectionsUtils.getRandom(obstacles);
            pf.add(getRandomEmptyState(model), p);
        }
        //put final goal over the grid
        pf.add(getRandomEmptyState(model), new Proposition("exit"));
        model.setPropositionFunction(pf);
        //create reward function 
        model.setRewardFunction(ToRL.convertRewardFunction(model, BAD_REWARD,
                RoverModel.getBadRewardObstacles()));
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);
        final Map<Integer, State> map = CollectionsUtils.asIndexMap(initStates);

        return new Problem<ERG>(model, map);
    }
}
