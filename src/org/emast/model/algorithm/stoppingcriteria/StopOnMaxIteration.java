package org.emast.model.algorithm.stoppingcriteria;

import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class StopOnMaxIteration implements StoppingCriterium {

    @Override
    public boolean isStopEpisodes(IterationValues values) {
        return values.getIterations() == MAX_ITERATIONS;
    }
}
