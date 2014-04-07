package org.jalt.model.test.erg.city;

import java.util.Arrays;
import java.util.List;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 *
 * @author And
 */
public class CityProblemFactory extends ProblemFactory {

    private final int rows;
    private final int cols;
    private final int agents;
    private final int numberOfObstacles;
    private final int numberOfBridges;
    private final int numberOfSemaphores;

    public CityProblemFactory(final int pRows,
            final int pCols, final int pAgents,
            final int pNumberOfObstacles,
            final int pNumberOfSemaphores,
            final int pNumberOfBridges) {
        rows = pRows;
        cols = pCols;
        agents = pAgents;
        numberOfObstacles = pNumberOfObstacles;
        numberOfSemaphores = pNumberOfSemaphores;
        numberOfBridges = pNumberOfBridges;
    }

    @Override
    protected Problem doCreate() {
        final CityModel model = new CityModel(rows, cols, agents);

        final Proposition hole = new Proposition("hole");
        final Proposition wall = new Proposition("wall");
        final Proposition bridge = new Proposition("bridge");
        final Proposition semaphore = new Proposition("semaphore");
        final Proposition exit = new Proposition("exit");

        final PropositionFunction pf = new PropositionFunction();
        final List<Proposition> props = Arrays.asList(wall, hole);
        //spread obstacles over the grid
        for (int i = 0; i < numberOfObstacles; i++) {
            final Proposition p = CollectionsUtils.getRandom(props);
            pf.add(getRandomEmptyState(model), p);
        }
        //distribute semaphores over the grid
        for (int i = 0; i < numberOfSemaphores; i++) {
            pf.add(getRandomEmptyState(model), semaphore);
        }
        //distribute bridges over the grid
        for (int i = 0; i < numberOfBridges; i++) {
            pf.add(getRandomEmptyState(model), bridge);
        }
        model.setPropositionFunction(pf);
        //put goal over the grid
        pf.add(getRandomEmptyState(model), exit);
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);
        //create initial states
        return new Problem(model, CollectionsUtils.asIndexMap(initStates));
    }
}
