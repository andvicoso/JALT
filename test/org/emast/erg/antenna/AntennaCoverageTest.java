package org.emast.erg.antenna;

import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class AntennaCoverageTest extends Test {

    private static final AntennaCoverageProblemFactory factory = new AntennaCoverageProblemFactory();

    public AntennaCoverageTest() {
        super(createProblem(), new PPFERG());
    }

    private static Problem createProblem() {
        final double antennasRatio = 0.025;
        final double obstaclesRatio = 0.2;
        final double agentsRatio = 0.02;
        final int rows = 10;
        final int cols = rows;
        final int agents = (int) (rows * cols * agentsRatio);
        final int obstacles = (int) (rows * cols * obstaclesRatio);
        final int antennas = (int) (rows * cols * antennasRatio);
        final int antennaRadius = 3;

        final Problem problem = factory.createProblem(rows, cols,
                agents, obstacles, antennas, antennaRadius);

        return problem;
    }

    public static void main(final String[] pArgs) {
        new AntennaCoverageTest().run();
    }
}
