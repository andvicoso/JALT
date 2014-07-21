package org.jalt.util;

import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class PolicyUtils {

	public static final String POLICY_STR = "policy";
	public static final String BEST_VALUES_STR = "best_values";

	private PolicyUtils() {
	}

	/**
	 * Create random policy
	 * 
	 * @param pModel
	 * @return
	 */
	public static Policy createRandom(final MDP pModel) {
		final Action[] actions = pModel.getActions().toArray(new Action[0]);
		final Random rand = new Random();
		final Policy policy = new Policy();

		for (final State state : pModel.getStates()) {
			final int randPosition = rand.nextInt(pModel.getActions().size());
			Action action = actions[randPosition];
			policy.put(state, action);
		}

		return policy;
	}

//	public static Policy join(List<Policy> policies) {
//		Policy ret = new Policy();
//		for (Policy policy : policies) {
//			ret.join(policy);
//		}
//		return ret;
//	}

	public static List<State> getPlanStates(MDP model, Policy pi, State init) {
		Stack<State> states = new Stack<State>();
		State state = init;
		Action action = null;
		do {
			action = pi.get(state);
			state = model.getTransitionFunction().getNextState(model.getStates(), state, action);

			if (action != null && state != null) {
				states.push(state);
			}
		} while (action != null && state != null);

		if (!states.isEmpty())
			states.pop();

		return states;
	}
}
