package org.emast.model.problem;

import java.util.Map;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;
import org.emast.util.Utils;

/**
 *
 * @author Anderson
 */
public class Problem<M extends MDP> {

    private double error = 0.009;
    private Map<Integer, State> initialStates;
    private M model;

    public Problem(M model, Map<Integer, State> initialStates) {
        this.model = model;
        this.initialStates = initialStates;
    }

    public Map<Integer, State> getInitialStates() {
        return initialStates;
    }

    public M getModel() {
        return model;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public void save() {
        final String filename = getClass().getSimpleName()
                + Utils.toFileTimeString(System.currentTimeMillis()) + ".emastp";
        Utils.toFile(this, filename);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nInitial states: ").append(initialStates);

        if (model instanceof GridModel) {
            final GridPrinter gridPrinter = new GridPrinter();
            final String grid = gridPrinter.print((GridModel) model, initialStates, null);
            sb.append("\n").append(grid);
        }

        return sb.toString();
    }

    public String toString(Object pResult) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nResult: ").append(pResult);

        if (model instanceof GridModel) {
            final GridPrinter gridPrinter = new GridPrinter();
            final String grid = gridPrinter.print((GridModel) model, initialStates, pResult);
            sb.append("\n").append(grid);
        }

        return sb.toString();
    }
}
