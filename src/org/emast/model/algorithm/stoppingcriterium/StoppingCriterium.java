package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationValues;

/**
 *
 * @author andvicoso
 */
public interface StoppingCriterium {

    boolean isStop(IterationValues iterationValues);
}
