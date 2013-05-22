package org.emast;

import org.emast.erg.antenna.AntennaExamples;
import org.emast.erg.generic.GenericERGProblemFactory;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.controller.ERGLearningBlockEachBadExp;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.algorithm.iteration.rl.DynaQ;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.BatchTest;
import org.emast.model.test.Test;
import org.emast.util.ProblemsCLI;

/**
 *
 * @author anderson
 */
public class MainTest {

    private static Problem createFromCLI() {
        //AntennaCoverageProblemFactory.createDefaultFactory();
        ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();
        ProblemsCLI rpg = new ProblemsCLI(factory);

        return rpg.run();
    }

    private static Algorithm createAlgorithm() {
        return new DynaQ();
    }

    private static Algorithm createERGAlgorithm() {
        return new ERGLearningBlockEachBadExp((AbstractRLearning<ERG>) createAlgorithm());
    }

    public static void main(final String[] pArgs) {
        Problem p = createFromCLI();//AntennaExamples.getSBIC13();//
        Test test = new Test(p) {
            @Override
            protected Algorithm createAlgorithm() {
                return MainTest.createERGAlgorithm();
            }
        };
        test.run();
    }
}
