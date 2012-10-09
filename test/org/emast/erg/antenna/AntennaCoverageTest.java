package org.emast.erg.antenna;

import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class AntennaCoverageTest {

    private static Problem createProblem() {
        double antennasRatio = 0.025;
        double obstaclesRatio = 0.2;
        double agentsRatio = 0.02;
        int rows = 10;
        int cols = rows;
        int agents = (int) (rows * cols * agentsRatio);
        int obstacles = (int) (rows * cols * obstaclesRatio);
        int antennas = (int) (rows * cols * antennasRatio);
        int antennaRadius = 3;

        ProblemFactory factory = new AntennaCoverageProblemFactory(rows, cols,
                agents, obstacles, antennas, antennaRadius);

        return factory.create();
    }

    public static void main(String[] pArgs) {
        new Test(createProblem(), new PPFERG()).run();
    }
}
