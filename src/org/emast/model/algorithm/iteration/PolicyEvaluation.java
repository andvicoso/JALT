package org.emast.model.algorithm.iteration;

import java.util.HashMap;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.SinglePolicy;
import org.emast.model.state.State;
import org.emast.util.DefaultTestProperties;

/**
 * Policy evaluation in sutton and barto introduction book. Figure 4.1.
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
		final SinglePolicy pi = (SinglePolicy) pParameters.get("policy");
		double delta;
		// Start the main loop
		do {
			delta = 0;
			values = new HashMap<State, Double>();
			// for each state
			for (final State state : model.getStates()) {
				double v = lastv.get(state);
				double current = getValue(model, pi, lastv, state);
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

	private double getValue(MDP model, SinglePolicy policy, Map<State, Double> values, State state) {
		Action action = policy.get(state);
		double r = model.getRewardFunction().getValue(state, action);
		double v = r + getGama()
				* getSum(model.getTransitionFunction(), model.getStates(), state, action, values);

		return v;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
}
