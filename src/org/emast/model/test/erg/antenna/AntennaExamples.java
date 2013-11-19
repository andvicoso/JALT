package org.emast.model.test.erg.antenna;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.model.converter.ToRL;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.grid.GridUtils;
import org.emast.util.grid.distancemeasure.CityBlock;

/**
 * 
 * @author Anderson
 */
public class AntennaExamples {
	
	private static final int BAD_REWARD = -30;
	private static final int ANTENNA_SIGNAL = 2;

	public AntennaExamples() {
	}

	public static Problem<ERG> getSMC13(Map<Integer, State> initialStates) {
		final AntennaCoverageModel model = new AntennaCoverageModel(5, 5, initialStates.size());
		//set a preservation goal different from the original
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
		pf.add(GridUtils.createGridState(1, 0), hole);
		pf.add(GridUtils.createGridState(1, 1), hole);
		pf.add(GridUtils.createGridState(0, 3), water);
		pf.add(GridUtils.createGridState(2, 4), stone);
		pf.add(GridUtils.createGridState(3, 1), stone);
		pf.add(GridUtils.createGridState(0, 4), exit);
		pf.add(GridUtils.createGridState(0, 4), up);
		// distribute antennas over the grid
		pf.add(GridUtils.createGridState(2, 0), antenna);
		pf.add(GridUtils.createGridState(0, 3), antenna);

		model.setPropositionFunction(pf);

		AntennaCoverageProblemFactory.setAntennaCoverage(model.getStates(), pf, antenna, coverage,
				ANTENNA_SIGNAL, new CityBlock());

		final Set<State> finalStates = new HashSet<State>();
		finalStates.add(GridUtils.createGridState(0, 4));

		model.setRewardFunction(ToRL.convertRewardFunction(model, BAD_REWARD,
				Arrays.asList(water, stone, hole)));

		return new Problem<ERG>(model, initialStates, finalStates);
	}

	public static Problem<ERG> getSMC13() {
		final Map<Integer, State> initialStates = new HashMap<Integer, State>();
		initialStates.put(0, GridUtils.createGridState(4, 0));

		return getSMC13(initialStates);
	}

	public static Problem<ERG> getSMC13Multi() {
		final Map<Integer, State> initialStates = new HashMap<Integer, State>();
		initialStates.put(0, GridUtils.createGridState(4, 0));
		initialStates.put(1, GridUtils.createGridState(0, 0));
		initialStates.put(2, GridUtils.createGridState(4, 4));

		return getSMC13(initialStates);
	}
}
