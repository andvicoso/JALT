package org.emast.erg.marsrover;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class MarsRoverProblemFactory extends ProblemFactory {

    public Problem createProblem(final int pRows, final int pCols,
            final int pAgents, final int pNumberOfObstacles) {
        //create model
        final MarsRoverModel model = new MarsRoverModel(pRows, pCols, pAgents);
        //create props
        final Proposition hole = new Proposition("hole");
        final Proposition stone = new Proposition("stone");
        final Proposition water = new Proposition("water");
        final Proposition exit = new Proposition("exit");
        //create proposition function
        final PropositionFunction pf = new PropositionFunction();
        final List<Proposition> props = Arrays.asList(water, stone, hole);
        //spread obstacles all over the grid
        for (int i = 0; i < pNumberOfObstacles; i++) {
            final Proposition p = getRandom(props);
            pf.add(getRandomEmptyState(model), p);
        }
        //put final goal over the grid
        pf.add(getRandomEmptyState(model), exit);
        model.setPropositionFunction(pf);
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, pAgents);
        final Map<Integer, State> map = CollectionsUtils.asMap(initStates);

        return new Problem(model, map);
    }
}
