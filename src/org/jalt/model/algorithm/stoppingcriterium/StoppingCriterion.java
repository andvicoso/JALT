package org.jalt.model.algorithm.stoppingcriterium;

import org.jalt.model.algorithm.rl.dp.IterationValues;

/**
 *
 * @author andvicoso
 */
public interface StoppingCriterion {

    boolean isStop(IterationValues iterationValues);
}
