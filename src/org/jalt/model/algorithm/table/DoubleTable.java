package org.jalt.model.algorithm.table;

import java.util.Collection;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class DoubleTable extends StateActionTable<Double> {

    public DoubleTable(DoubleTable q) {
        super(q);
    }

    public DoubleTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions, 0d);
    }

    public DoubleTable(Collection<State> states, Collection<Action> actions, Double initialValue) {
        super(states, actions, initialValue);
    }

    @Override
    protected String formatValue(State state, Action action, Double value) {
        return String.format("%.4g", value);
    }
}
