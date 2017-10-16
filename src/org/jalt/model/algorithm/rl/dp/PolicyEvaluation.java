package org.jalt.model.algorithm.rl.dp;

import java.util.HashMap;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.stoppingcriterium.StopOnMaxDiffError;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;

/**
 * Policy evaluation in sutton and barto introduction book. Figure 4.1.
 * 
 * @author andvicoso
 * 
 */
public class PolicyEvaluation<M extends MDP> extends IterationAlgorithm<M, Map<State, Double>> implements IterationValues {

	private static final double ERROR = 0.009;
	private Map<State, Double> lastv;
	private Map<State, Double> v;
	private Policy policy;

	public PolicyEvaluation(Policy policy) {
		stoppingCriterium = new StopOnMaxDiffError(ERROR);
		this.policy = policy;
	}

	@Override
	public Map<State, Double> run(Problem<M> pProblem, Map<String, Object> pParameters) {
		model = pProblem.getModel();
		v = new HashMap<State, Double>(model.getStates().size());
		initializeV(pProblem, v);

		MDP model = pProblem.getModel();
		double delta;
		// Start the main loop
		do {
			delta = 0;
			lastv = v;
			v = new HashMap<State, Double>(model.getStates().size());
			// for each state
			for (final State state : model.getStates()) {
				Action action = policy.getBestAction(state);
				if (action != null) {
					double value = lastv.get(state);
					Double currentValue = getValue(model, state, action, lastv);
					if (currentValue != null) {
						v.put(state, currentValue);
						double diff = Math.abs(value - currentValue);
						delta = Math.max(diff, delta);
					}
				}
			}

			episodes++;
		} while (stoppingCriterium.isStop(this));

		return v;
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
