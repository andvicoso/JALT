package org.emast.util;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class GridUtils {

    private static final String GRID_STATE_SEP = "x";

    private GridUtils() {
    }

    public static List<State> createStates(final int pRows, final int pCols) {
        final List<State> states = new ArrayList<State>();
        for (int i = 0; i < pRows; i++) {
            for (int j = 0; j < pCols; j++) {
                String stName = getGridStateName(i, j);
                states.add(new State(stName));
            }
        }
        return states;
    }

    public static State createGridState(final int pRow, final int pCol) {
        return new State(getGridStateName(pRow, pCol));
    }

    public static String getGridStateName(final int pRow, final int pCol) {
        String srow = Integer.toString(pRow);
        String scol = Integer.toString(pCol);
        int size = Math.max(srow.length(), scol.length());
        String format = "%0" + size + "d";

        return String.format(format, pRow) + GRID_STATE_SEP + String.format(format, pCol);
    }

    public static List<Action> createGridMovementActions() {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new Action("north"));
        actions.add(new Action("south"));
        actions.add(new Action("west"));
        actions.add(new Action("east"));

        return actions;
    }

    public static int getRow(final State pState) {
        return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[0]);
    }

    public static int getCol(final State pState) {
        return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[1]);
    }

    public static int getCityBlockDistance(final State pS1, final State pS2) {
        return Math.abs(getRow(pS1) - getRow(pS2)) + Math.abs(getCol(pS1) - getCol(pS2));
    }
}
