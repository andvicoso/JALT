package org.jalt.model.test.erg.antenna;

import static org.jalt.util.DefaultTestProperties.OTHERWISE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunctionProposition;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.grid.distancemeasure.CityBlock;

/**
 * 
 * @author andvicoso
 */
public class AntennaExamples {

	private static final double BAD_REWARD = -30;
	private static final double GOOD_REWARD = -BAD_REWARD;
	private static final int ANTENNA_SIGNAL = 2;

	public AntennaExamples() {
	}

	public static Problem<ERG> getSMC13(Map<Integer, State> initialStates) {
		final AntennaCoverageModel model = new AntennaCoverageModel(5, 5, initialStates.size());
		// set a preservation goal different from the original
		model.setPreservationGoal(new Expression("coverage"));

		final Proposition hole = new Proposition("hole");
		final Proposition stone = new Proposition("stone");
		final Proposition water = new Proposition("water");
		final Proposition antenna = new Proposition("antenna");
		final Proposition coverage = new Proposition("coverage");
		final Proposition exit = new Proposition("exit");
		final Proposition up = new Proposition("up");
		// spread obstacles over the grid
		final PropositionFunction pf = new PropositionFunction();
		pf.add(new GridState(1, 0), hole);
		pf.add(new GridState(1, 1), hole);
		pf.add(new GridState(0, 3), water);
		pf.add(new GridState(2, 4), stone);
		pf.add(new GridState(3, 1), stone);
		pf.add(new GridState(0, 4), exit);
		pf.add(new GridState(0, 4), up);
		// distribute antennas over the grid
		pf.add(new GridState(2, 0), antenna);
		pf.add(new GridState(0, 3), antenna);

		model.setPropositionFunction(pf);

		AntennaCoverageProblemFactory.setAntennaCoverage(model.getStates(), pf, antenna, coverage,
				ANTENNA_SIGNAL, new CityBlock());

		final Set<State> finalStates = new HashSet<State>();
		finalStates.add(new GridState(0, 4));

		Map<Proposition, Double> rew = CollectionsUtils.createMap(
				AntennaCoverageProblemFactory.getBadRewardObstacles(), BAD_REWARD);
		rew.put(exit, GOOD_REWARD);
		// set bad reward function
		model.setRewardFunction(new RewardFunctionProposition(model, rew, OTHERWISE));

		return new Problem<ERG>(model, initialStates, finalStates);
	}

	public static Problem<ERG> getSMC13() {
		final Map<Integer, State> initialStates = new HashMap<Integer, State>();
		initialStates.put(0, new GridState(4, 0));

		return getSMC13(initialStates);
	}

	public static Problem<ERG> getSMC13Multi() {
		final Map<Integer, State> initialStates = new HashMap<Integer, State>();
		initialStates.put(0, new GridState(4, 0));
		initialStates.put(1, new GridState(0, 0));
		initialStates.put(2, new GridState(4, 4));

		return getSMC13(initialStates);
	}
}
