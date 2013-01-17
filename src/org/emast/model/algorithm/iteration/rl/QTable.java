package org.emast.model.algorithm.iteration.rl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class QTable extends NTable {

    public QTable(NTable q) {
        super(q);
    }

    public QTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions);
    }

    public QTable(List<State> pStates, List<Action> pActions) {
        super(pStates, pActions);
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : states) {
            double max = -Double.MAX_VALUE;
            for (Action action : actions) {
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

        for (State state : states) {
            for (Action action : actions) {
                double value = get(state, action);
                if (pAddZeros || value != 0) {
                    policy.put(state, action, value);
                }
            }
        }

        return policy;
    }
}
