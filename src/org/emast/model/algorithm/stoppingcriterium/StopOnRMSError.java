package org.emast.model.algorithm.stoppingcriterium;

import static org.emast.util.DefaultTestProperties.ERROR;

import org.emast.model.algorithm.iteration.IterationError;
import org.emast.model.algorithm.iteration.IterationValues;

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
		double currentError = IterationError.rmse(values.getLastValues(),
				values.getCurrentValues(), values.getCurrentValues().keySet());
		// compare with predefined error
		return currentError < error;
	}
}
