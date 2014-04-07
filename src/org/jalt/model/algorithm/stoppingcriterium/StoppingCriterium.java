package org.jalt.model.algorithm.stoppingcriterium;

import org.jalt.model.algorithm.iteration.IterationValues;

/**
 *
 * @author andvicoso
 */
public interface StoppingCriterium {

    boolean isStop(IterationValues iterationValues);
}
