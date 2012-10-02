package org.emast.erg.marsrover;

import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class MarsRoverTest extends Test {

    public MarsRoverTest() {
        super(createProblem(), new PPFERG());
    }

    private static Problem createProblem() {
        final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();
        final int rows = 9;
        final int cols = 9;
        final int size = rows * cols;
        final int agents = (int) (0.15 * size);
        final int obstacles = (int) (0.3 * size);

        return factory.createProblem(rows, cols, agents, obstacles);
    }

    public static void main(final String[] pArgs) {
        new MarsRoverTest().run();
    }
}
