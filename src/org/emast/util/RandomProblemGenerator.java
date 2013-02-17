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

    private static final boolean startWithLastValid = true;
    public static final String DEFAULT_SUFFIX = "problem.emast";
    public static final String DEFAULT_DIR = "problems";
    private final ProblemFactory factory;

    public RandomProblemGenerator(ProblemFactory pFactory) {
        factory = pFactory;
    }

    public Problem run() {
        Scanner reader = new Scanner(System.in);
        Problem p;

        do {
            p = startWithLastValid ? getLastValid() : factory.create();
            if (p == null) {
                p = factory.create();
            }
            System.out.println("Problem: " + p.toString());
            System.out.print("console: ");

            String r = reader.nextLine();
            if (r.equals("r") || (r.equals("s") && save(p))) {
                break;
            }
        } while (true);

        return p;
    }

    private boolean save(Problem p) {
        String modelName = p.getModel().getClass().getSimpleName();
        String dir = DEFAULT_DIR + File.separator + modelName + File.separator;
        String filename = dir + DEFAULT_SUFFIX;

        return FileUtils.toFile(p, filename, true);
    }

    private Problem getLastValid() {
        File last = getLastModified(DEFAULT_DIR + File.separator, 0l);
        return last != null ? FileUtils.fromFile(last.getAbsolutePath()) : null;
    }

    public File getLastModified(String path, Long lastModified) {
        File root = new File(path);
        File[] list = root.listFiles();
        File last = null;

        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    File dir_last = getLastModified(f.getAbsolutePath(), lastModified);
                    f = dir_last;
                }
                if (f.lastModified() > lastModified) {
                    last = f;
                    lastModified = last.lastModified();
                }
            }
        }

        return last;
    }
}
