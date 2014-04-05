package org.emast.model.algorithm.iteration;

import java.util.Map;
import java.util.Set;

import org.emast.model.state.State;

/**
 * 
 * @author andvicoso
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

	public static double rmse(Map<State, Double> best, Map<State, Double> current,
			Set<State> visited) {
		double sum = 0;
		int count = 0;
		// goes through the visited states
		for (State state : visited) {
			// get the best value for any action
			Double v1 = best.get(state);
			v1 = v1 == null ? 0.0 : v1;
			Double v2 = current.get(state);
			double diff = Math.abs(v1 - v2);
			sum += (diff * diff);
			count++;
			// Log.info(String.format("%s->%f", state, diff));
		}

		return Math.sqrt(sum / count);
	}
}
