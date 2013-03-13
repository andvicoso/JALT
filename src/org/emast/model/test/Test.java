package org.emast.model.test;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.problem.Problem;
import org.emast.util.Utils;

/**
 *
 * @author And
 */
public class Test implements Runnable {

    private Problem problem;
    private Algorithm[] algorithms;
    private long msecs;

    public Test(Problem pProblem, Algorithm... pAlgorithms) {
        problem = pProblem;
        algorithms = pAlgorithms;
    }

    @Override
    public void run() {
        if (problem != null) {

            print("\nModel:");
            print(problem.getModel().toString());
            print("\n\nProblem:");
            print(problem.toString());
            print("\nExecution:");

            long testInitMsecs = System.currentTimeMillis();
            for (Algorithm algorithm : algorithms) {
                print("\n------------------------------");
                String algorithmName = algorithm.getClass().getSimpleName();
                print("\nAlgorithm: " + algorithmName);
                println();
                //execute
                long initMsecs = System.currentTimeMillis();
                Object result = algorithm.run(problem);
                long diff = System.currentTimeMillis() - initMsecs;
                //print time
                print("\nTime: ");
                print(toTimeString(diff));
                //print results
                print(algorithm.printResults());
                //if a solution was found...
                if (result != null) {
                    print("\n\nResult:");
                    print(problem.toString(result));
                }
            }
            msecs = System.currentTimeMillis() - testInitMsecs;
            //print time
            print("\nTotal Test Time: ");
            print(toTimeString(msecs));
        }
    }

    public static String toTimeString(final long pMsecs) {
        return Utils.toTimeString(pMsecs) + "(" + pMsecs + " ms)";
    }

    public long getMsecs() {
        return msecs;
    }

    protected void print(String pMsg) {
        Log.info(pMsg);
    }

    protected void println() {
        print("\n");
    }
}
