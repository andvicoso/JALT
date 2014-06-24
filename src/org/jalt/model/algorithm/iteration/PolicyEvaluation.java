package org.jalt.model.algorithm.iteration;

import java.util.HashMap;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.SinglePolicy;
import org.jalt.model.state.State;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.PolicyUtils;

/**
 * Policy evaluation in sutton and barto introduction book. Figure 4.1.
 * 
 * @author andvicoso
 * 
 */
public class PolicyEvaluation<M extends MDP> extends IterationAlgorithm<M, Map<State, Double>> {

	private Map<State, Double> lastv;
	private Map<State, Double> v;

	public Map<State, Double> run(Problem<M> pProblem, Map<String, Object> pParameters) {
		model = pProblem.getModel();
		v = new HashMap<State, Double>(model.getStates().size());
		initializeV(pProblem, v);

		SinglePolicy pi = (SinglePolicy) pParameters.get(PolicyUtils.POLICY_STR);
		MDP model = pProblem.getModel();
		double delta;
		// Start the main loop
		do {
			delta = 0;
			lastv = v;
			v = new HashMap<State, Double>();
			// for each state
			for (final State state : model.getStates()) {
				Action action = pi.get(state);
				double value = lastv.get(state);
				double currentValue = getValue(model, state, action, lastv);
				v.put(state, currentValue);

				double diff = Math.abs(value - currentValue);
				delta = Math.max(diff, delta);
			}

			episodes++;
		} while (delta > DefaultTestProperties.ERROR);

		return v;
	}
}
