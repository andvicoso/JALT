package org.emast.model.algorithm.iteration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.stoppingcriterium.StopOnMaxDiffError;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.DefaultTestProperties;
import org.emast.util.grid.GridPrinter;

/**
 * Reinforcement Learning Survey 96 Kaelbling,Littman,Moore
 * 
 * @author andvicoso
 * 
 */
public class ValueIteration<M extends MDP> extends IterationAlgorithm<M, Policy> implements
		PolicyGenerator<M>, IterationValues {
	private Map<State, Double> lastv = Collections.emptyMap();
	private Map<State, Double> v = Collections.emptyMap();
	private StoppingCriterium stoppingCriterium = new StopOnMaxDiffError(0.000001);
	private Policy pi;

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		model = pProblem.getModel();
		episodes = -1;
		v = new HashMap<State, Double>();
		// Start the main loop
		// When the maximmum error is greater than the defined error,
		// the best policy is found
		do {
			lastv = v;
			episodes++;
			// set initial v
			v = new HashMap<State, Double>();
			// create the policy
			pi = new Policy();
			for (State state : pProblem.getFinalStates()) {
				// double rew = model.getRewardFunction().getValue(state, null);
				lastv.put(state, DefaultTestProperties.GOOD_REWARD);
			}

			// for each state
			for (State state : model.getStates()) {
				if (!pProblem.getFinalStates().contains(state)) {
					Map<Action, Double> q = getQ(model, state, lastv);
					// if found some action and value
					if (!q.isEmpty()) {
						// get the max value for q
						Double max = Collections.max(q.values());
						// save the max value
						v.put(state, max);
						// add to the policy
						pi.put(state, q);
					}
				}
			}
			// Log.info("\n"+printResults());
			// Log.info("\n" + new GridPrinter().toTable(v, 10, 10));
			// Log.info("\n"+pProblem.toString(pi));
		} while (!stoppingCriterium.isStop(this));

		//Log.info("Iterations: " + episodes);
		// Log.info("\n"+printResults());
		//Log.info("\n" + pProblem.toString(pi.getBestPolicy()));

		return pi;
	}

	@Override
	public String printResults() {
		String lvs;
		String vs;

		if (model instanceof Grid) {
			int rows = ((Grid) model).getRows();
			int cols = ((Grid) model).getCols();
			lvs = new GridPrinter().toTable(lastv, rows, cols);
			vs = new GridPrinter().toTable(v, rows, cols);
		} else {
			lvs = lastv.toString();
			vs = lastv.toString();
		}

		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("\nValues:\n").append(vs);
		sb.append("\nLast values:\n").append(lvs);

		return sb.toString();
	}

	@Override
	public Map<State, Double> getCurrentValues() {
		return v;
	}

	@Override
	public Map<State, Double> getLastValues() {
		return lastv;
	}
}
