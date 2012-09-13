package org.emast.model.algorithm;

import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Algorithm<M extends MDP, R> {

    R run(Problem<M> pProblem);

    String printResults();
}
