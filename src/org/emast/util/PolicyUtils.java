package org.emast.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationError;
import org.emast.model.algorithm.iteration.PolicyEvaluation;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

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
			policy.put(state, action, 0d);
		}

		return policy;
	}

	public static Policy join(List<Policy> policies) {
		Policy ret = new Policy();
		for (Policy policy : policies) {
			ret.join(policy);
		}
		return ret;
	}

	/**
	 * @param bv
	 *            best values (from value iteration)
	 * @param qt
	 *            after running VI over the real/complete model the policy
	 *            returned comprises only the visited states
	 */
	public static <M extends MDP, QT extends QTable<? extends QTableItem>> boolean comparePolicies(
			Map<State, Double> bv, QT qt, M oldModel) {
		Policy pi = qt.getPolicy(false);
		Map<State, Double> qv = extractV(oldModel, pi);
		// calculate root-mean-square error (RMSE)
		double error = IterationError.rmse(bv, qv, pi.getStates());
		// compare with predefined error
		return error > DefaultTestProperties.ERROR;
	}

	public static Map<State, Double> extractV(MDP model, Policy current) {
		PolicyEvaluation pe = new PolicyEvaluation();
		return pe.run(new Problem<MDP>(model, null),
				CollectionsUtils.asMap(POLICY_STR, current.getBestPolicy()));
	}

}
