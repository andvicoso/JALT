package org.emast.model.algorithm.iteration;

import java.util.HashMap;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.SinglePolicy;
import org.emast.model.state.State;
import org.emast.util.DefaultTestProperties;
import org.emast.util.PolicyUtils;

/**
 * Policy evaluation in sutton and barto introduction book. Figure 4.1.
 * 
 * @author andvicoso
 * 
 */
public class PolicyEvaluation extends IterationAlgorithm<MDP, Map<State, Double>> {

	public Map<State, Double> run(Problem<MDP> pProblem, Map<String, Object> pParameters) {
		MDP model = pProblem.getModel();
		Map<State, Double> values;
		Map<State, Double> lastv = new HashMap<State, Double>();
		// initialize pi and values
		for (final State state : model.getStates()) {
			lastv.put(state, 0d);
		}
		final SinglePolicy pi = (SinglePolicy) pParameters.get(PolicyUtils.POLICY_STR);
		double delta;
		// Start the main loop
		do {
			delta = 0;
			values = new HashMap<State, Double>();
			// for each state
			for (final State state : model.getStates()) {
				Action action = pi.get(state);
				double v = lastv.get(state);
				double current = getValue(model, state, action, lastv);
				double diff = Math.abs(v - current);
				values.put(state, current);

				if (diff > delta)
					delta = diff;
			}
			lastv = values;
			episodes++;
		} while (delta > DefaultTestProperties.ERROR);

		return values;
	}
}
