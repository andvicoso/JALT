package org.emast.model.function.transition;

import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public class GridTransitionFunction extends TransitionFunction {

    private final int rows;
    private final int cols;

    public GridTransitionFunction(int pRows, int pCols) {
        rows = pRows;
        cols = pCols;
    }

    private Map<State, Action> getTransitions(State pState) {
        return getTransitions(GridUtils.getRow(pState), GridUtils.getCol(pState));
    }

    private Map<State, Action> getTransitions(int pRow, int pCol) {
        final Map<State, Action> possibleMovs = new HashMap<State, Action>(4);
        final Action south = new Action("south");
        final Action east = new Action("east");
        final Action west = new Action("west");
        final Action north = new Action("north");

        if (pRow + 1 < rows) {
            possibleMovs.put(GridUtils.createGridState((pRow + 1), (pCol)), south);
        }
        if (pCol + 1 < cols) {
            possibleMovs.put(GridUtils.createGridState((pRow), (pCol + 1)), east);
        }
        if (pRow - 1 >= 0) {
            possibleMovs.put(GridUtils.createGridState((pRow - 1), (pCol)), north);
        }
        if (pCol - 1 >= 0) {
            possibleMovs.put(GridUtils.createGridState((pRow), (pCol - 1)), west);
        }

        return possibleMovs;
    }

    @Override
    public double getValue(State pState, State pFinalState, Action pAction) {
        final Map<State, Action> targets = getTransitions(pState);

        for (Map.Entry<State, Action> entry : targets.entrySet()) {
            State state = entry.getKey();
            Action action = entry.getValue();

            if (Action.isValid(pAction, action)
                    && State.isValid(state, pFinalState)) {
                return 1d / targets.size();
            }
        }

        return 0;
    }
}