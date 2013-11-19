package org.emast.model.algorithm;

import java.util.Map;

import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Algorithm<M extends MDP, R> {

    R run(Problem<M> pProblem, Map<String, Object> pParameters);

    String printResults();

    public String getName();
}
