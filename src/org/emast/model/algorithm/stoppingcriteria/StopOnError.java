package org.emast.model.algorithm.stoppingcriteria;

import org.emast.model.algorithm.iteration.IterationError;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class StopOnError implements StoppingCriterium {

    @Override
    public boolean isStopEpisodes(IterationValues values) {
        double currentError = IterationError.getError(values.getIterations(),
                values.getLastValues(), values.getCurrentValues());
        return currentError < ERROR;
    }
}
