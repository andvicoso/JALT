package org.jalt.test.erg.hunterprey;

import java.util.HashSet;
import java.util.List;

import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 *
 * @author And
 */
public class HunterPreyProblemFactory extends ProblemFactory<GridModel> {

	private final int rows;
	private final int cols;
	private final int agents;
	private final int preys;

	public HunterPreyProblemFactory(final int pRows, final int pCols, final int pAgents, final int pPreys) {
		rows = pRows;
		cols = pCols;
		agents = pAgents;
		preys = pPreys;
	}

	@Override
	protected Problem<GridModel> doCreate() {
		final GridModel model = new GridModel(rows, cols);
		// create initial states
		final List<State> initStates = getRandomEmptyStates(model, agents);
		// create final state
		final List<State> finalStates = getRandomEmptyStates(model, preys);

		return new Problem<GridModel>(model, CollectionsUtils.asIndexMap(initStates), new HashSet<State>(finalStates));
	}
}
