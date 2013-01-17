package org.emast;

import org.emast.erg.antenna.AntennaCoverageProblemFactory;
import org.emast.erg.antenna.AntennaExamples;
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
        String problem = "";
        String model = "AntennaCoverageModel";//"RoverModel";
        String CURRENT_PROBLEM = "problems/" + model + "/" + problem + "problem.emast";
        Problem p = FileUtils.fromFile(CURRENT_PROBLEM);

        return p;
    }

    public static Problem create() {
        return new AntennaExamples().getAAAI2013NoInitialPreserv();
    }

    public static Problem createRandom() {
        ProblemFactory factory = AntennaCoverageProblemFactory.createDefaultFactory();
        RandomProblemGenerator rpg = new RandomProblemGenerator(factory);

        return rpg.run();
    }
}
