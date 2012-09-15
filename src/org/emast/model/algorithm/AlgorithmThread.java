package org.emast.model.algorithm;

import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public class AlgorithmThread<M extends MDP, R> extends Thread {

    private final Algorithm<M, R> algorithm;
    private final Problem<M> problem;
    private R result;

    public AlgorithmThread(Algorithm<M, R> pAlgorithm, Problem<M> pProblem) {
        super(pAlgorithm.getClass().getSimpleName()
                + " - " + pProblem.getClass().getSimpleName());
        algorithm = pAlgorithm;
        problem = pProblem;
    }

    @Override
    public void run() {
        result = algorithm.run(problem);
    }

    public R getResult() {
        return result;
    }
}
