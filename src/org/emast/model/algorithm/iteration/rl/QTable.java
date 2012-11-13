package org.emast.model.algorithm.iteration.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;

/**
 *
 * @author Anderson
 */
public class QTable {

    private List<State> states;
    private List<Action> actions;
    private Double[][] values;

    public QTable(QTable q) {
        this(q.getStates(), q.getActions());

        for (int i = 0; i < states.size(); i++) {
            System.arraycopy(q.getValues()[i], 0, values[i], 0, actions.size());
        }
    }

    public QTable(List<State> pStates, List<Action> pActions) {
        states = pStates;
        actions = pActions;
        values = new Double[states.size()][actions.size()];

        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < actions.size(); j++) {
                values[i][j] = -Double.MAX_VALUE;
            }
        }
    }

    public QTable(Collection<State> states, Collection<Action> actions) {
        this(new ArrayList<State>(states), new ArrayList<Action>(actions));
    }

    Double[][] getValues() {
        return values;
    }

    List<Action> getActions() {
        return actions;
    }

    List<State> getStates() {
        return states;
    }

    public double get(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return values[si][ai];
    }

    public void put(State state, Action action, Double value) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);
        values[si][ai] = value;
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : states) {
            double max = 0;
            Action best = null;
            for (Action action : actions) {
                double value = get(state, action);
                if (value >= max) {
                    max = value;
                    best = action;
                }
            }
            if (best != null) {
                map.put(state, max);
            }
        }

        return map;
    }

    public Policy getPolicy() {
        final Policy policy = new Policy();

        for (State state : states) {
            Double max = null;
            Action best = null;
            for (Action action : actions) {
                double value = get(state, action);
                if (max == null || value >= max) {
                    max = value;
                    best = action;
                }
            }
            if (best != null) {
                policy.put(state, best);
            }
        }

        return policy;
    }

    public String[][] toTable() {
        String[][] table = new String[states.size() + 1][actions.size() + 1];
        int i = 1;
        for (State state : states) {
            table[i++][0] = state.getName();
        }
        int j = 1;
        for (Action action : actions) {
            table[0][j++] = action.getName();
        }

        i = 1;
        for (State state : states) {
            j = 1;
            for (Action action : actions) {
                Double value = get(state, action);
                table[i][j] = String.format("%.4e", value);
                j++;
            }
            i++;
        }

        return table;
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable());
    }
}
