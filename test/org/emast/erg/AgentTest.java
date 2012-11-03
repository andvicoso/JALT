package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.converter.Reinforcement;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.Test;

/**
 *
 * @author anderson
 */
public class AgentTest {

    private static Problem createProblem() {
        Problem p = CurrentProblem.create();
        MDP model = p.getModel();

        if (model instanceof Reinforcement) {
            model = ((Reinforcement) model).toReinforcement();
        }

        return ProblemFactory.create(p, model);
    }

    public static void main(final String[] pArgs) {
        new Test(createProblem(), new ValueIterationAlgorithm()).run();
    }
}
