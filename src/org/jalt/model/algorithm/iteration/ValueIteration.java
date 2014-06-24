package org.jalt.model.algorithm.iteration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.action.Action;
import org.jalt.model.algorithm.PolicyGenerator;
import org.jalt.model.algorithm.stoppingcriterium.StopOnMaxDiffError;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridPrinter;

/**
 * Reinforcement Learning Survey 96 Kaelbling,Littman,Moore
 * 
 * @author andvicoso
 * 
 */
public class ValueIteration<M extends MDP> extends IterationAlgorithm<M, Policy> implements
		PolicyGenerator<M>, IterationValues {
	private Map<State, Double> lastv;
	private Map<State, Double> v;
	private StoppingCriterium stoppingCriterium = new StopOnMaxDiffError(0.000001);

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		Policy pi;

		model = pProblem.getModel();
		v = new HashMap<State, Double>(model.getStates().size());
		initializeV(pProblem, v);
		// Start the main loop
		// When the maximmum error is greater than the defined error,
		// the best policy is found
		do {
			lastv = v;
			v = new HashMap<State, Double>();
			pi = new Policy();
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

			episodes++;

			// Log.info("\n"+pProblem.toString(pi));
		} while (!stoppingCriterium.isStop(this));

		// Log.info("Iterations: " + episodes);
		// Log.info("\n"+printResults());
		// Log.info("\n" + pProblem.toString(pi.getBestPolicy()));

		int size = (int) Math.sqrt(model.getStates().size());
		Log.info("\n" + new GridPrinter().toTable(v, size, size));

		return pi;
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
