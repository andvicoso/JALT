package org.emast.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
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

    private static <M extends MDP & Grid> Map<State, Action> getTransitions(M pModel, State pState) {
        return getTransitions(pModel, GridUtils.getRow(pState), GridUtils.getCol(pState));
    }

    private static Action getAction(MDP pModel, String pActionName) {
        for (Action action : pModel.getActions()) {
            if (action.getName().equals(pActionName)) {
                return action;
            }
        }

        return new Action(pActionName);
    }

    private static <M extends MDP & Grid> Map<State, Action> getTransitions(M pModel, int pRow, int pCol) {
        final Map<State, Action> possibleMovs = new HashMap<State, Action>(4);
        final Action south = getAction(pModel, "south");
        final Action east = getAction(pModel, "east");
        final Action west = getAction(pModel, "west");
        final Action north = getAction(pModel, "north");

        if (pRow + 1 < pModel.getRows()) {
            possibleMovs.put(getState(pModel, (pRow + 1), (pCol)), south);
        }
        if (pCol + 1 < pModel.getCols()) {
            possibleMovs.put(getState(pModel, (pRow), (pCol + 1)), east);
        }
        if (pRow - 1 >= 0) {
            possibleMovs.put(getState(pModel, (pRow - 1), (pCol)), north);
        }
        if (pCol - 1 >= 0) {
            possibleMovs.put(getState(pModel, (pRow), (pCol - 1)), west);
        }

        return possibleMovs;
    }

    public static <M extends MDP & Grid> TransitionFunction createTransitionFunction(final M pModel) {
        return new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                final Map<State, Action> targets = getTransitions(pModel, pState);

                for (Map.Entry<State, Action> entry : targets.entrySet()) {
                    State state = entry.getKey();
                    Action action = entry.getValue();

                    if (isValidAction(pAction, action)
                            && isValidState(state, pFinalState)) {
                        return 1d / targets.size();
                    }
                }

                return 0;
            }
        };
    }

    private static State getState(MDP pModel, final int pRow, final int pCol) {
        return getState(pModel, GridUtils.getGridStateName(pRow, pCol));
    }

    private static State getState(MDP pModel, String pStateName) {
        for (State state : pModel.getStates()) {
            if (state.getName().equals(pStateName)) {
                return state;
            }
        }

        return new State(pStateName);
    }
}
