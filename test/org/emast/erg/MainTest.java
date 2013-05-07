package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.controller.ERGQLearningController4;
import org.emast.model.algorithm.iteration.rl.DynaQ;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class MainTest implements Runnable {

    public Problem getProblem() {
        return CurrentProblem.create();
    }

    public Algorithm[] getAlgorithms() {
        Algorithm[] algs = new Algorithm[]{
            new QLearning(),
            new DynaQ(),
            new ERGQLearningController4()
        };
        return algs;
    }

    @Override
    public void run() {
        new Test(getProblem(), getAlgorithms()).run();
    }

    public static void main(final String[] pArgs) {
        final MainTest mainTest = new MainTest();
        mainTest.run();
    }
}
