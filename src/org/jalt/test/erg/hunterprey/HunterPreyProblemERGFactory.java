package org.jalt.test.erg.hunterprey;

import java.util.Arrays;
import java.util.List;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 *
 * @author And
 */
public class HunterPreyProblemERGFactory extends ProblemFactory<ERGGridModel> {

	private final int rows;
	private final int cols;
	private final int agents;
	private final int numberOfObstacles;

	public HunterPreyProblemERGFactory(final int pRows, final int pCols, final int pAgents, final int pNumberOfObstacles) {
		rows = pRows;
		cols = pCols;
		agents = pAgents;
		numberOfObstacles = pNumberOfObstacles;
	}

	@Override
	protected Problem<ERGGridModel> doCreate() {
		final ERGGridModel model = new ERGGridModel(rows, cols);

		final Proposition hole = new Proposition("hole");
		final Proposition wall = new Proposition("wall");
		final Proposition prey = new Proposition("prey");

		final PropositionFunction pf = new PropositionFunction();
		final List<Proposition> props = Arrays.asList(wall, hole);
		// spread obstacles over the grid
		for (int i = 0; i < numberOfObstacles; i++) {
			final Proposition p = CollectionsUtils.getRandom(props);
			pf.add(getRandomEmptyState(model), p);
		}
		// add prey
		pf.add(getRandomEmptyState(model), prey);

		model.setPropositionFunction(pf);
		// create initial states
		final List<State> initStates = getRandomEmptyStates(model, agents);

		return new Problem<ERGGridModel>(model, CollectionsUtils.asIndexMap(initStates));
	}
}
