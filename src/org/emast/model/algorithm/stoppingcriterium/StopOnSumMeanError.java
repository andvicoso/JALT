package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationError;
import org.emast.model.algorithm.iteration.IterationValues;

import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
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
        double currentError = IterationError.getSumMeanError(values.getLastValues(), values.getCurrentValues());
        return currentError < error;
    }
}
