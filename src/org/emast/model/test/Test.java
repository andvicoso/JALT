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
    private Algorithm algorithm;
    private long msecs;

    public Test(Problem pProblem, Algorithm pAlgorithm) {
        problem = pProblem;
        algorithm = pAlgorithm;
    }

    protected void init() {
        out.println("Problem:\n");
        out.println(problem.toString());
        out.println("\nExecution:");
    }

    @Override
    public void run() {
        init();

        long initMsecs = System.currentTimeMillis();
        Object result = algorithm.run(problem);
        msecs = System.currentTimeMillis() - initMsecs;

        out.println("\nTime: ");
        out.println(toTimeString(msecs));

        //if a solution was found...
        if (result != null) {
            out.println(result.toString() + "\n");
        }
    }

    public static String toTimeString(final long pMsecs) {
        return Utils.toTimeString(pMsecs) + "(" + pMsecs + " ms)";
    }

    public long getMsecs() {
        return msecs;
    }
}
