package org.jalt.model.algorithm.stoppingcriterium;

import static org.jalt.util.DefaultTestProperties.ERROR;

import java.util.Map;

import org.jalt.model.algorithm.iteration.IterationValues;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class StopOnSumMeanError implements StoppingCriterium {

	private double error = ERROR;

	public StopOnSumMeanError() {
	}

	public StopOnSumMeanError(double pError) {
		error = pError;
	}

	@Override
	public boolean isStop(IterationValues values) {
		double currentError = getSumMeanError(values.getLastValues(), values.getCurrentValues());
		return currentError < error;
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
}
