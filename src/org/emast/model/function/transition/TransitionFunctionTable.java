package org.emast.model.function.transition;

import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.NTable;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class TransitionFunctionTable extends TransitionFunction {

    private NTable table;
    private State[][] finalStates;

    public TransitionFunctionTable(Collection<State> pStates, Collection<Action> pActions) {
        table = new NTable(pStates, pActions);
    }

    public TransitionFunctionTable(List<State> pStates, List<Action> pActions) {
        table = new NTable(pStates, pActions);
    }

    public State getState(final State pState, final Action pAction) {
        int si = table.getStates().indexOf(pState);
        int ai = table.getActions().indexOf(pAction);
        return finalStates[si][ai];
    }

    public double getValue(final State pState, final Action pAction) {
        Double v = table.get(pState, pAction);
        return v == null ? 0d : v;
    }

    public void put(State state, Action action, State finalState, Double value) {
        int si = table.getStates().indexOf(state);
        int ai = table.getActions().indexOf(action);
        table.getValues()[si][ai] = value;
        finalStates[si][ai] = finalState;
    }

    public State[][] getFinalStates() {
        return finalStates;
    }

    @Override
    public double getValue(State pState, State pFinalState, Action pAction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}