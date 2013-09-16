package org.emast.model.test;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.controller.ERGLearningBlockEachBadExp;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.erg.antenna.AntennaExamples;
import org.emast.model.test.erg.generic.GenericERGProblemFactory;
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
        return new QLearning();
    }

    private static Algorithm createERGAlgorithm() {
        return new ERGLearningBlockEachBadExp((AbstractRLearning<ERG>) createAlgorithm());
    }

    public static void main(final String[] pArgs) {
        Problem p = AntennaExamples.getSBIC13();//createFromCLI();//
        Test test = new Test(p) {
            @Override
            protected Algorithm createAlgorithm() {
                return MainTest.createERGAlgorithm();
            }
        };
        test.run();
    }
}
