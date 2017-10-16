package org.jalt.model.algorithm.stoppingcriterium;

import static org.jalt.util.DefaultTestProperties.ERROR;

import java.util.Map;

import org.jalt.model.algorithm.rl.dp.IterationValues;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class StopOnRMSError implements StoppingCriterium {

	private double error = ERROR;

	public StopOnRMSError() {
	}

	public StopOnRMSError(double pError) {
		error = pError;
	}

	@Override
	public boolean isStop(IterationValues values) {
		// calculate root-mean-square error (RMSE)
		double currentError = rmse(values.getLastValues(), values.getCurrentValues());
		// compare with predefined error
		return currentError < error;
	}

	public static double rmse(Map<State, Double> best, Map<State, Double> current) {
		double sum = 0;
		int count = 0;
		// goes through the visited states
		for (State state : current.keySet()) {
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
