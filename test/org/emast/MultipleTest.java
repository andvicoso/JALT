package org.emast;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author Anderson
 */
public class MultipleTest {

    private static Algorithm[] createAlgorithms() {
        return new Algorithm[]{new PPFERG(), new ValueIterationAlgorithm()};
    }

    private static Problem createProblem() {
        return FileUtils.fromFile("problems/RoverModel/problem9.emast");
    }

    public static void main(String[] args) {
        new Test(createProblem(), createAlgorithms()).run();
    }
}
