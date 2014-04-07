package org.jalt.model.algorithm;

import org.jalt.model.model.MDP;
import org.jalt.model.solution.Policy;

/**
 *
 * @author andvicoso
 */
public interface PolicyGenerator<M extends MDP> extends Algorithm<M, Policy> {
}
