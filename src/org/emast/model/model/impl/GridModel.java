package org.emast.model.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.agent.Agent;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.Grid;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public abstract class GridModel extends MDPModel implements Grid {

    private static final String GRID_STATE_SEP = "x";
    private int rows;
    private int cols;

    public GridModel(int pRows, int pCols, int pAgents) {
        super(createStates(pRows, pCols), createGridMovementActions(),
                CollectionsUtils.createList(Agent.class, pAgents));
        this.rows = pRows;
        this.cols = pCols;
    }

    public GridModel(final GridModel pModel) {
        super(pModel);
        this.rows = pModel.getRows();
        this.cols = pModel.getCols();
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    public static List<Action> createGridMovementActions() {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new Action("north"));
        actions.add(new Action("south"));
        actions.add(new Action("west"));
        actions.add(new Action("east"));

        return actions;
    }

    private Map<State, Action> getTransitions(final State pState) {
        return getTransitions(getRow(pState), getCol(pState));
    }

    private Map<State, Action> getTransitions(int pRow, int pCol) {
        final Map<State, Action> possibleMovs = new HashMap<State, Action>(4);
        final Action south = getAction("south");
        final Action east = getAction("east");
        final Action west = getAction("west");
        final Action north = getAction("north");

        if (pRow + 1 < rows) {
            possibleMovs.put(getState((pRow + 1), (pCol)), south);
        }
        if (pCol + 1 < cols) {
            possibleMovs.put(getState((pRow), (pCol + 1)), east);
        }
        if (pRow - 1 >= 0) {
            possibleMovs.put(getState((pRow - 1), (pCol)), north);
        }
        if (pCol - 1 >= 0) {
            possibleMovs.put(getState((pRow), (pCol - 1)), west);
        }

        return possibleMovs;
    }

    @Override
    public TransitionFunction getTransitionFunction() {
        return new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                final Map<State, Action> targets = getTransitions(pState);
                final double value = 1d / targets.size();

                return value;
            }
        };
    }

    private State getState(final int pRow, final int pCol) {
        return getState(getGridStateName(pRow, pCol));
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
