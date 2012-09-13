package org.emast.model.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.agent.Agent;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.Grid;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public abstract class GridModel extends MDPModel implements Grid {

    private int rows;
    private int cols;

    public GridModel(int pRows, int pCols, int pAgents) {
        super(GridUtils.createStates(pRows, pCols), createGridMovementActions(),
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
    public GridModel copy() {
        return new GridModel(this) {
            @Override
            public RewardFunction getRewardFunction() {
                return GridModel.this.getRewardFunction();
            }
        };
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
        return getTransitions(GridUtils.getRow(pState), GridUtils.getCol(pState));
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
                double value = 0;

                if (targets.values().contains(pAction)) {
                    value = 1d / targets.size();
                }

                return value;
            }
        };
    }

    private State getState(final int pRow, final int pCol) {
        return getState(GridUtils.getGridStateName(pRow, pCol));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("\nRows: ").append(getRows());
        sb.append("\nCols: ").append(getCols());

        return sb.toString();
    }
}
