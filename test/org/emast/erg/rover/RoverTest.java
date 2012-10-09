package org.emast.erg.rover;

import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author anderson
 */
public class RoverTest {

    private static Problem createProblem() {
        final int rows = 9;
        final int cols = 9;
        final int size = rows * cols;
        final int agents = (int) (0.15 * size);
        final int obstacles = (int) (0.3 * size);

        //ProblemFactory factory = new RoverProblemFactory(rows, cols, agents, obstacles);
//        RandomProblemGenerator rpg = new RandomProblemGenerator(createFactory());
//        Problem p = rpg.run();
//
//        return p;
        return FileUtils.fromFile("problems/RoverModel/problem9.emast");
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), new PPFERG()).run();
    }
}
