package org.emast.model.function.transition;

import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class GridTransitionFunctionState extends GridTransitionFunction {

    private final Collection<State> finalStates;
    private final Collection<State> obstacles;

    public GridTransitionFunctionState(int pRows, int pCols,
            Collection<State> pObstacles, Collection<State> pFinalStates) {
        super(pRows, pCols);
        finalStates = pFinalStates;
        obstacles = pObstacles;
    }

    @Override
    public double getValue(State pState, State pFinalState, Action pAction) {
        if (obstacles.contains(pFinalState) || finalStates.contains(pState) || obstacles.contains(pState)) {
            return 0.0d;
        }
        return super.getValue(pState, pFinalState, pAction);
    }
}
