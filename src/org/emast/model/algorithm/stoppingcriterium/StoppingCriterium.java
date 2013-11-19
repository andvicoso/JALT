package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationValues;

/**
 *
 * @author Anderson
 */
public interface StoppingCriterium {

    boolean isStop(IterationValues iterationValues);
}
