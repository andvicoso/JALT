package org.jalt.util.grid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;

/**
 *
 * @author andvicoso
 */
public class GridModelWriter {

    private final Problem problem;
    private final String filename;

    public GridModelWriter(String filename, Problem problem) {
        this.filename = filename;
        this.problem = problem;
    }

    public void write() throws IOException {
        Writer fw = new BufferedWriter(new FileWriter(filename));
        MDP model = problem.getModel();

        if (model instanceof GridModel) {
            final GridPrinter gridPrinter = new GridPrinter();
            String[][] grid = gridPrinter.getGrid((GridModel) model,
                    problem.getInitialStates(), problem.getFinalStates());
            String smodel = gridPrinter.toTable(grid);

            fw.write(smodel);
        }

        fw.close();
    }
}
