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

    private static PrintStream out = System.out;
    private Problem problem;
    private Algorithm[] algorithms;
    private long msecs;

    public Test(Problem pProblem, Algorithm... pAlgorithms) {
        problem = pProblem;
        algorithms = pAlgorithms;
    }

    @Override
    public void run() {
        out.print("\nModel:");
        out.print(problem.getModel().toString());
        out.print("\n\nProblem:");
        out.print(problem.toString());
        out.print("\nExecution:");

        for (Algorithm algorithm : algorithms) {
            out.print("\nAlgorithm: ");
            out.print(algorithm.getClass().getSimpleName());
            out.println();
            //execute
            long initMsecs = System.currentTimeMillis();
            Object result = algorithm.run(problem);
            msecs = System.currentTimeMillis() - initMsecs;
            //print time
            out.print("\nTime: ");
            out.print(toTimeString(msecs));
            out.print(algorithm.printResults());
            //if a solution was found...
            if (result != null) {
                out.print("\n\nResult:");
                out.print(problem.toString(result));
            }
        }
    }

    public static String toTimeString(final long pMsecs) {
        return Utils.toTimeString(pMsecs) + "(" + pMsecs + " ms)";
    }

    public long getMsecs() {
        return msecs;
    }
}
