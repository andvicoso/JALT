package org.emast;

import org.emast.erg.generic.GenericERGProblemFactory;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.util.FileUtils;
import org.emast.util.ProblemsCLI;

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
        //DynaSuttonMazeTests.getERGFigure95Example();//
        return createFromCLI();//new AntennaExamples().getAAAI2013NoInitialPreserv();
    }

    public static Problem createFromCLI() {
        ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();//AntennaCoverageProblemFactory.createDefaultFactory();
        ProblemsCLI rpg = new ProblemsCLI(factory);

        return rpg.run();
    }
}
