package org.emast;

import org.emast.erg.rover.RoverProblemFactory;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.util.FileUtils;
import org.emast.util.RandomProblemGenerator;

/**
 *
 * @author Anderson
 */
public class CurrentProblem {

    public static Problem createFromFile() {
        String CURRENT_PROBLEM = "problems/RoverModel/53_problem.emast";
        Problem p = FileUtils.fromFile(CURRENT_PROBLEM);

        return p;
    }

    public static Problem create() {
        return createFromFile();
    }

    public static Problem createRandom() {
        ProblemFactory factory = RoverProblemFactory.createDefaultFactory();
        RandomProblemGenerator rpg = new RandomProblemGenerator(factory);

        return rpg.run();
    }
}
