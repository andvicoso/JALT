package org.jalt.model.algorithm.iteration;

import java.util.Collections;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.PolicyGenerator;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.util.PolicyUtils;

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
					// save max value and position in the policy
					if (current != null && max > current && !pi.get(state).equals(q.get(max))) {
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
