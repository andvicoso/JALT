package org.jalt.model.algorithm.stoppingcriterium;

import static org.jalt.util.DefaultTestProperties.ERROR;

import java.util.Map;

import org.jalt.model.algorithm.iteration.IterationValues;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class StopOnMaxDiffError implements StoppingCriterium {

	private double error = ERROR;

	public StopOnMaxDiffError() {
	}

	public StopOnMaxDiffError(double pError) {
		error = pError;
	}

	@Override
	public boolean isStop(IterationValues values) {
		double maxDiff = -Double.MAX_VALUE;
		int n = values.getIterations();
		Map<State, Double> lastv = values.getLastValues();
		Map<State, Double> v = values.getCurrentValues();

		if (n == 0) {
			maxDiff = Double.MAX_VALUE;
		} else {
			for (State state : lastv.keySet()) {
				Double val1 = lastv.get(state);
				Double val2 = v.get(state);
				val1 = val1 == null ? 0 : val1;
				val2 = val2 == null ? 0 : val2;

				double diff = Math.abs(val2 - val1);
				if (diff > maxDiff) {
					maxDiff = diff;
				}
			}
		}

		// Log.info("Error: " + String.format("%.4g", maxDif));

		return maxDiff < error;
	}
}
