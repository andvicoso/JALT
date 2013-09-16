package org.emast.util.grid;

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
    public static final String GRID_STATE_FORMAT_PREFFIX = "%0";
    public static final String GRID_STATE_FORMAT_SUFFIX = "d";
    public static final String ZERO = "0";
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String WEST = "west";
    public static final String EAST = "east";
    public static final Action south = new Action("south");
    public static final Action east = new Action("east");
    public static final Action west = new Action("west");
    public static final Action north = new Action("north");
    public static final List<Action> GRID_ACTIONS = new ArrayList<Action>();

    static {
        GRID_ACTIONS.add(north);
        GRID_ACTIONS.add(south);
        GRID_ACTIONS.add(west);
        GRID_ACTIONS.add(east);
    }

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
        String format = GRID_STATE_FORMAT_PREFFIX + size + GRID_STATE_FORMAT_SUFFIX;

        return String.format(format, pRow) + GRID_STATE_SEP + String.format(format, pCol);
    }

    public static int getRow(final State pState) {
        return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[0]);
    }

    public static int getCol(final State pState) {
        return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[1]);
    }
}
