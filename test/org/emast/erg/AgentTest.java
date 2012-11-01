package org.emast.erg;

import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author anderson
 */
public class AgentTest {

    private static Problem createProblem() {
        //ProblemFactory factory = new RoverProblemFactory(rows, cols, agents, obstacles);
//        RandomProblemGenerator rpg = new RandomProblemGenerator(createFactory());
//        Problem p = rpg.run();
//
//        return p;
        return FileUtils.fromFile(AgentGroupTest.CURRENT_PROBLEM);
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), new ValueIterationAlgorithm()).run();
    }
}
