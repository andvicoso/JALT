package org.emast.model.algorithm.stoppingcriteria;

/**
 *
 * @author Anderson
 */
public interface StoppingCriterium {

    boolean isStopEpisodes(IterationValues iterationValues);
}
