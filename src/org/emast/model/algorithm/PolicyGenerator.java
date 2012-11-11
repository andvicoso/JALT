package org.emast.model.algorithm;

import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public interface PolicyGenerator<M extends MDP> extends Algorithm<M, Policy> {
}
