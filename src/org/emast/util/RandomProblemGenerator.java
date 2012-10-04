package org.emast.util;

import java.io.File;
import java.util.Scanner;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;

/**
 *
 * @author Anderson
 */
public class RandomProblemGenerator {

    private final ProblemFactory factory;

    public RandomProblemGenerator(ProblemFactory pFactory) {
        factory = pFactory;
    }

    public Problem run() {
        Scanner reader = new Scanner(System.in);
        Problem p;
        String r;

        do {
            p = factory.create();
            System.out.println("Problem: " + p.toString());
            System.out.print("console: ");

            r = reader.nextLine();
            if (r.equals("s") && save(p)) {
                break;
            }
        } while (true);

        return p;
    }

    private boolean save(Problem p) {
        String modelName = p.getModel().getClass().getSimpleName();
        String dir = "problems" + File.separator + modelName + File.separator;
        String filename = dir + "problem.emast";
        return FileUtils.toFile(p, filename, true);
    }
}
