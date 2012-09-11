package org.nemast.erg.marsrover;

import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class MarsRoverTest extends Test {

    public MarsRoverTest() {
        super(createProblem(), new ValueIterationAlgorithm());
    }

    private static Problem createProblem() {
        final int rows = 9;
        final int cols = 9;
        final int agents = 5;
        final int obstacles = 35;
        final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();
        return factory.createProblem(rows, cols, agents, obstacles);
    }

    public static void main(final String[] pArgs) {
        new MarsRoverTest().run();
    }
}
