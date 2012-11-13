package org.emast.model.model.impl;

import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.GridTransitionFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.Grid;
import org.emast.model.state.State;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public class GridModel extends MDPModel implements Grid {

    private int rows;
    private int cols;

    public GridModel() {
    }

    public GridModel(int rows, int cols, TransitionFunction transitionFunction,
            RewardFunction rewardFunction, Collection<State> states,
            Collection<Action> actions, int agents) {
        super(transitionFunction, rewardFunction, states, actions, agents);
        this.rows = rows;
        this.cols = cols;
    }

    public GridModel(int pRows, int pCols) {
        this.rows = pRows;
        this.cols = pCols;
        setStates(GridUtils.createStates(pRows, pCols));
        setActions(GridUtils.createGridMovementActions());
        setTransitionFunction(new GridTransitionFunction(rows, cols));
    }

    @Override
    public GridModel copy() {
        return new GridModel(rows, cols, getTransitionFunction(),
                getRewardFunction(), getStates(), getActions(), getAgents());
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("\nGrid Size: ");
        sb.append(getRows()).append("x").append(getCols());

        return sb.toString();
    }
}
