package org.emast.model.test;

import java.io.PrintStream;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.problem.Problem;
import org.emast.util.Utils;

/**
 *
 * @author And
 */
public class Test implements Runnable {

    private static final boolean DEBUG = true;
    private static final PrintStream DEBUG_WRITER = System.out;
    private Problem problem;
    private Algorithm[] algorithms;
    private long msecs;

    public Test(Problem pProblem, Algorithm... pAlgorithms) {
        problem = pProblem;
        algorithms = pAlgorithms;
    }

    @Override
    public void run() {
        print("\nModel:");
        print(problem.getModel().toString());
        print("\n\nProblem:");
        print(problem.toString());
        print("\nExecution:");

        for (Algorithm algorithm : algorithms) {
            print("\nAlgorithm: ");
            print(algorithm.getClass().getSimpleName());
            println();
            //execute
            long initMsecs = System.currentTimeMillis();
            Object result = algorithm.run(problem);
            msecs = System.currentTimeMillis() - initMsecs;
            //print time
            print("\nTime: ");
            print(toTimeString(msecs));
            //print results
            print(algorithm.printResults());
            //if a solution was found...
            if (result != null) {
                print("\n\nResult:");
                print(problem.toString(result));
            }
        }
    }

    public static String toTimeString(final long pMsecs) {
        return Utils.toTimeString(pMsecs) + "(" + pMsecs + " ms)";
    }

    public long getMsecs() {
        return msecs;
    }

    protected void print(String pMsg) {
        if (DEBUG) {
            DEBUG_WRITER.print(pMsg);
        }
    }

    protected void println() {
        print("\n");
    }
}
