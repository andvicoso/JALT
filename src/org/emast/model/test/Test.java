package org.emast.model.test;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SinglePolicy;
import org.emast.util.DefaultTestProperties;
import org.emast.util.Utils;

/**
 *
 * @author And
 */
public class Test implements Runnable {

    protected static final boolean DEBUG = true;
    protected Algorithm algorithm;
    protected Problem problem;

    public Test(Problem pProblem, Algorithm pAlgorithm) {
        this(pProblem);
        algorithm = pAlgorithm;
    }

    public Test(Problem pProblem) {
        problem = pProblem;
    }

    @Override
    public void run() {
        print("\n################################");
        print("\nModel:");
        print(problem.getModel().toString());
        print("\nError: " + DefaultTestProperties.ERROR);
        print("\nProblem:");
        print(problem.toString());
        print("\nExecution:");

        createAndRun();
    }

    protected Object runAlg(Problem problem, Algorithm algorithm) {
        //execute
        long initMsecs = System.currentTimeMillis();
        Object result = algorithm.run(problem);
        long diff = System.currentTimeMillis() - initMsecs;
        //print time
        print("Time: " + Utils.toTimeString(diff));
        //print results
        printNoInitialBreak(algorithm.printResults());
        //if a solution was found...
        if (result != null) {
            print("Result:" + problem.toString(result));
            SinglePolicy sp = ((Policy) result).getBestPolicy();
            print("Single Result:" + problem.toString(sp));
        }

        return result;
    }

    protected void print(String pMsg) {
        if (DEBUG) {
            Log.info(pMsg);
        }
    }

    protected void println() {
        print("\n");
    }

    protected void printNoInitialBreak(String str) {
        print(str.startsWith("\n") ? str.substring(1) : str);
    }

    protected Algorithm createAlgorithm() {
        return new QLearning();
    }

    protected void createAndRun() {
        if (algorithm == null) {
            algorithm = createAlgorithm();
        }

        print("\n------------------------------");
        String algorithmName = algorithm.getName();
        print("Algorithm: " + algorithmName);
        //run
        runAlg(problem, algorithm);
    }
}
