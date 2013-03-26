package org.emast.model.algorithm.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class QTable extends DoubleTable {

    public QTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions);
    }

    public QTable(QTable q) {
        super(q);
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable());
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : getStates()) {
            double max = -Double.MAX_VALUE;
            for (Action action : getActions()) {
                double value = get(state, action);
                if (value != 0 && value >= max) {
                    max = value;
                }
            }
            map.put(state, max == -Double.MAX_VALUE ? 0 : max);
        }

        return map;
    }

    public Policy getPolicy(boolean pAddZeros) {
        final Policy policy = new Policy();

        for (State state : getStates()) {
            for (Action action : getActions()) {
                double value = get(state, action);
                if (pAddZeros || value != 0) {
                    policy.put(state, action, value);
                }
            }
        }

        return policy;
    }
}
