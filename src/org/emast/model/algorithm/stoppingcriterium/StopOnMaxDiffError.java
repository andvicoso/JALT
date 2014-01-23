package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationError;
import org.emast.model.algorithm.iteration.IterationValues;

import static org.emast.util.DefaultTestProperties.*;

/**
 * 
 * @author Anderson
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
		double currentError = IterationError.getMaxDiffError(values.getIterations(),
				values.getLastValues(), values.getCurrentValues());
		return currentError <  error;
	}
}
