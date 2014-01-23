package org.emast.model.algorithm.iteration;

import java.util.Collections;
import java.util.HashMap;
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

	private double gama = 0.9d;
	private int iterations = 0;

	@Override
	public Policy run(Problem<MDP> pProblem, Map<String, Object> pParameters) {
		MDP model = pProblem.getModel();
		boolean changed;
		final Policy pi = PolicyUtils.createRandom(model);
		// Start the main loop
		do {
			final Map<State, Double> values = evaluatePolicy(model, pi);
			changed = false;
			// for each state
			for (final State state : model.getStates()) {
				Map<Action, Double> q = getQ(model, state, values);
				// if found some action and value
				if (!q.isEmpty()) {
					// get the max value for q
					Double max = Collections.max(q.values());
					final Double current = getSum(model, values, state, pi.getBestAction(state));
					// save the max value and position in the policy
					if (max > current && !pi.get(state).equals(q.get(max))) {
						values.put(state, max);
						// pi.put(state, q);
						changed = true;
					}
				}
			}
			iterations++;
		} while (changed);

		return pi;
	}

	private Map<State, Double> evaluatePolicy(MDP pModel, Policy policy) {
		final Map<State, Double> values = new HashMap<State, Double>();
		for (final State state : policy.getStates()) {
			Action bestAction = policy.getBestAction(state);
			Double x = pModel.getRewardFunction().getValue(state, bestAction);
			x += getGama() * getSum(pModel, values, state, bestAction);

			values.put(state, x);
		}

		return values;
	}

	protected double getSum(MDP pModel, Map<State, Double> values, State pState, Action pAction) {
		double sum = 0;
		for (final State stateLine : pModel.getStates()) {
			final Double trans = pModel.getTransitionFunction()
					.getValue(pState, stateLine, pAction);

			if (trans != null && values.get(stateLine) != null) {
				sum += trans * values.get(stateLine);
			}
		}

		return sum;
	}

	public double getGama() {
		return gama;
	}

	@Override
	public String printResults() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\nIterations: ").append(iterations);

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
}
