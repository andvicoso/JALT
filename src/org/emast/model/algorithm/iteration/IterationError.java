package org.emast.model.algorithm.iteration;

import java.util.Map;
import java.util.Set;

import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import org.emast.util.DefaultTestProperties;

/**
 * 
 * @author Anderson
 */
public class IterationError {

	public static double getMaxDiffError(int n, Map<State, Double> lastv, Map<State, Double> v) {
		double maxDiff = -Double.MAX_VALUE;

		if (n == 0) {
			maxDiff = Double.MAX_VALUE;
		} else {
			for (State state : lastv.keySet()) {
				Double val1 = lastv.get(state);
				Double val2 = v.get(state);

				if (val1 == null || val2 == null) {
					break;
				}

				double diff = Math.abs(val2 - val1);
				if (diff > maxDiff) {
					maxDiff = diff;
				}
			}
		}

		// Log.info("Error: " + String.format("%.4g", maxDif));

		return maxDiff;
	}

	public static double getSumMeanError(Map<State, Double> optv, Map<State, Double> v) {
		double sum = 0;
		int count = 0;
		for (State state : optv.keySet()) {
			Double val1 = optv.get(state);
			Double val2 = v.get(state);

			if (val1 != null && val2 != null) {
				sum += Math.abs(val1 - val2);
				count++;
			}
		}

		double error = 1d / count * sum;
		return error;
	}

	public static <M extends MDP, QT extends QTable<? extends QTableItem>> boolean comparePolicies(
			Map<State, Double> bv, QT qt, M oldModel) {
		// best -> policy after running VI over the real/complete model
		// the policy returned comprises only the visited states (extracted from Q-Table)
		Policy pi = qt.getPolicy(false);
		Map<State, Double> qv = extractV(oldModel, pi);
		// calculate root-mean-square error (RMSE)
		double error = rmse(bv, qv, pi.getStates());
		// compare with predefined error
		return error > DefaultTestProperties.ERROR;
	}

	private static Map<State, Double> extractV(MDP model, Policy current) {
		PolicyEvaluation pe = new PolicyEvaluation();
		return pe.run(new Problem<MDP>(model, null),
				CollectionsUtils.asMap("policy", current.getBestPolicy()));
	}

	public static double rmse(Map<State, Double> best, Map<State, Double> current,
			Set<State> visited) {
		double sum = 0;
		int count = 0;
		// goes through the visited states
		for (State state : visited) {
			// get the best value for any action
			Double v1 = best.get(state);
			Double v2 = current.get(state);
			double diff = Math.abs(v1 - v2);
			sum += (diff * diff);
			count++;
			// Log.info(String.format("%s->%f", state, diff));
		}

		return Math.sqrt(sum / count);
	}
}
