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
        final StringBuilder sb = new StringBuilder();
        sb.append("\nModel:");
        sb.append(problem.getModel().toString());
        sb.append("\n\nProblem:");
        sb.append(problem.toString());
        sb.append("\nExecution:");

        for (Algorithm algorithm : algorithms) {
            sb.append("\nAlgorithm: ").append(algorithm.getClass().getSimpleName());
            //execute
            long initMsecs = System.currentTimeMillis();
            Object result = algorithm.run(problem);
            msecs = System.currentTimeMillis() - initMsecs;
            //print time
            sb.append("\nTime: ").append(toTimeString(msecs));
            sb.append(algorithm.printResults());
            //if a solution was found...
            if (result != null) {
                sb.append("\n\nResult:");
                sb.append(problem.toString(result));
            }
        }

        out.print(sb);
    }

    public static String toTimeString(final long pMsecs) {
        return Utils.toTimeString(pMsecs) + "(" + pMsecs + " ms)";
    }

    public long getMsecs() {
        return msecs;
    }
}
