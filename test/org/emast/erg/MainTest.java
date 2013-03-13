package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.algorithm.QLearningERGController;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class MainTest {

    private static Problem createProblem() {
        Problem p = CurrentProblem.create();
        //p = ReinforcementConverter.convert(p);
        return p;
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), new QLearningERGController()).run();// new ValueIterationAlgorithm(), 
    }
}