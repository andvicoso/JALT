package org.jalt.model.algorithm.iteration;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.SinglePolicy;
import org.jalt.model.state.State;
import org.jalt.util.PolicyUtils;

/**
 * Policy evaluation in sutton and barto introduction book. Figure 4.1.
 * 
 * @author andvicoso
 * 
 */
public class PolicyEvaluation<M extends MDP> extends IterationAlgorithm<M, Map<State, Double>> {

	private static final double ERROR = 0.009;
	private Map<State, Double> lastv;
	private Map<State, Double> v;

	public Map<State, Double> run(Problem<M> pProblem, Map<String, Object> pParameters) {
		model = pProblem.getModel();
		v =  new HashMap<State, Double>(model.getStates().size());//new TreeMap<State, Double>();
		initializeV(pProblem, v);

		SinglePolicy pi = (SinglePolicy) pParameters.get(PolicyUtils.POLICY_STR);
		MDP model = pProblem.getModel();
		double delta;
		// Start the main loop
		do {
			delta = 0;
			lastv = v;
			v =  new HashMap<State, Double>(model.getStates().size());//new TreeMap<State, Double>();
			// for each state
			for (final State state : model.getStates()) {
				Action action = pi.get(state);
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
		} while (delta > ERROR);

		return v;
	}
}
