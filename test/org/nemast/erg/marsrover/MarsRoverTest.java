package org.nemast.erg.marsrover;

import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class MarsRoverTest extends Test {

    private static final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();

    public MarsRoverTest() {
        super(createProblem(), new ValueIterationAlgorithm());
    }

    private static Problem createProblem() {
        final int rows = 5;
        final int cols = 5;
        final int agents = (int) (0.15 * rows * cols);
        final int obstacles = (int) (0.3 * rows * cols);

        return factory.createProblem(rows, cols, agents, obstacles);
    }

    public static void main(final String[] pArgs) {
        new MarsRoverTest().run();
    }
}
