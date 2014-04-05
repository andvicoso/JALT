package org.emast.model.algorithm.iteration;

import java.util.Collections;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.PolicyUtils;

public class PolicyIteration extends IterationAlgorithm<MDP, Policy> implements
		PolicyGenerator<MDP> {

	/**
	 * TEST - REVIEW
	 */
	@Override
	public Policy run(Problem<MDP> pProblem, Map<String, Object> pParameters) {
		MDP model = pProblem.getModel();
		boolean changed;
		final Policy pi = PolicyUtils.createRandom(model);
		// Start the main loop
		do {
			final Map<State, Double> v = new PolicyEvaluation<MDP>().run(pProblem, pParameters);
			changed = false;
			// for each state
			for (final State state : model.getStates()) {
				Map<Action, Double> q = getQ(model, state, v);
				// if found some action and value
				if (!q.isEmpty()) {
					// get the max value for q
					Double max = Collections.max(q.values());
					Double current = getValue(model, state, pi.getBestAction(state), v);
					// save the max value and position in the policy
					if (max > current && !pi.get(state).equals(q.get(max))) {
						v.put(state, max);
						// pi.put(state, q);
						changed = true;
					}
				}
			}
			episodes++;
		} while (changed);

		return pi;
	}
}
