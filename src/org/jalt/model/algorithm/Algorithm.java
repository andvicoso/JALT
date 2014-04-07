package org.jalt.model.algorithm;

import java.util.Map;

import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;

/**
 *
 * @author andvicoso
 */
public interface Algorithm<M extends MDP, R> {

    R run(Problem<M> pProblem, Map<String, Object> pParameters);

    String printResults();

    public String getName();
}
