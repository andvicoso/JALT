package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class AgentTest {

    private static Problem createProblem() {
        Problem p = CurrentProblem.create();
        //p = ReinforcementConverter.convert(p);
        return p;
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), EnsembleTest.createAlgorithm()).run();// new ValueIterationAlgorithm(), new QLearning()
    }
}
