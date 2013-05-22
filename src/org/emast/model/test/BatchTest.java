package org.emast.model.test;

import java.util.ArrayList;
import java.util.Collection;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.controller.AbstractERGLearning;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.problem.Problem;
import org.emast.util.CalcUtils;
import org.emast.util.Utils;

/**
 *
 * @author Anderson
 */
public class BatchTest extends Test {

    private int n = 30;

    public BatchTest(Problem pProblem) {
        super(pProblem);
    }

    public BatchTest(Problem pProblem, Algorithm pAlgorithm) {
        super(pProblem, pAlgorithm);
    }

    @Override
    protected void createAndRun() {
        long timeSum = 0;
        Collection<Integer> episodes = new ArrayList<Integer>();
        Collection<Double> steps = new ArrayList<Double>();
        Object result = null;
        
        for (int i = 0; i < n; i++) {
            print("Repetition: " + i);
            algorithm = createAlgorithm();
            //execute
            long initMsecs = System.currentTimeMillis();
            result = runAlg(problem, algorithm);
            long diff = System.currentTimeMillis() - initMsecs;
            timeSum += diff;

            if (algorithm instanceof AbstractERGLearning) {
                algorithm = ((AbstractERGLearning) algorithm).getLearning();
            }

            if (algorithm instanceof AbstractRLearning) {
                final AbstractRLearning ql = (AbstractRLearning) algorithm;
                episodes.add(ql.getIterations());
                steps.add(ql.getMeanSteps());
            }
        }

        double meanEps = CalcUtils.getMean(episodes);
        double meanSteps = CalcUtils.getMean(steps);

        print("Repetitions: " + n);
        print("Means: ");
        print("-Time: " + Utils.toTimeString(timeSum / n));
        print("-Episodes: " + meanEps);
        print("-Episodes (std deviation): " + CalcUtils.getStandardDeviation(meanEps, episodes));
        print("-Steps per episode: " + meanSteps);
        print("-Steps per episode (std deviation): " + CalcUtils.getStandardDeviation(meanSteps, steps));
//        //print results
//        printNoInitialBreak(algorithm.printResults());
//        if (result != null) {
//            print("Result:" + problem.toString(result));
//        }
    }
}
