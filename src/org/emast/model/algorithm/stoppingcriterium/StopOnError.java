package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationError;
import org.emast.model.algorithm.iteration.IterationValues;

import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class StopOnError implements StoppingCriterium {

    private double error = ERROR;

    public StopOnError() {
    }

    public StopOnError(double pError) {
        error = pError;
    }

    @Override
    public boolean isStop(IterationValues values) {
        double currentError = IterationError.getError(values.getIterations(),
                values.getLastValues(), values.getCurrentValues());
        return currentError < error;
    }
}
