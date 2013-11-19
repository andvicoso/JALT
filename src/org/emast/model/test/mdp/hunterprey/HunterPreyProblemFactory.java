package org.emast.model.test.mdp.hunterprey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emast.model.function.reward.RewardFunctionState;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * 
 * @author And
 */
public class HunterPreyProblemFactory extends ProblemFactory {

	private static final double OTHERWISE = -0.1;
	private static final double CAPTURE_REWARD = 1d;
	private final int rows;
	private final int cols;
	private final int agents;
	private final int preys;

	public HunterPreyProblemFactory(final int pRows, final int pCols, final int pAgents,
			final int pPreys) {
		rows = pRows;
		cols = pCols;
		agents = pAgents;
		preys = pPreys;
	}

	@Override
	protected Problem<MDP> doCreate() {
		final HunterPreyModel model = new HunterPreyModel(rows, cols, agents);
		// create initial and final states
		final List<State> initStates = getRandomEmptyStates(model, agents);
		final Set<State> finalStates = new HashSet<State>(getRandomEmptyStates(model, preys));

		model.setRewardFunction(new RewardFunctionState<MDP>(model, CollectionsUtils.createMap(
				finalStates, CAPTURE_REWARD), OTHERWISE));

		return new Problem<MDP>(model, CollectionsUtils.asIndexMap(initStates), finalStates);
	}
}
