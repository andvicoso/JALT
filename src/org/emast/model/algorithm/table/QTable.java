package org.emast.model.algorithm.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SimplePolicy;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;
import org.emast.util.grid.GridUtils;

/**
 *
 * @author Anderson
 */
public class QTable<I extends QTableItem> extends StateActionTable<I> {

    public QTable(Collection<State> states, Collection<Action> actions, I initialValue) {
        super(states, actions, initialValue);
    }

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

    public Policy getPolicy(boolean pAddZeros) {
        final Policy policy = new Policy();

        for (State state : getStates()) {
            for (Action action : getActions()) {
                double value = getValue(state, action);
                if (pAddZeros || value != 0) {
                    policy.put(state, action, value);
                }
            }
        }

        return policy;
    }

    public Set<State> getAllValidStates() {
        Set<State> valid = new HashSet<State>();

        for (Map.Entry<State, Map<Action, I>> entry : values.entrySet()) {
            State state = entry.getKey();
            Map<Action, I> actionsValues = entry.getValue();
            for (I item : actionsValues.values()) {
                if (item != null && item.getFrequency() != 0) {
                    valid.add(state);
                    break;
                }
            }
        }
        return valid;
    }

    public Map<Action, Double> getDoubleValues(State state) {
        Map<Action, I> qvalues = getValues(state);
        Map<Action, Double> dvalues = new HashMap<Action, Double>(qvalues.size());
        for (Map.Entry<Action, I> entry : qvalues.entrySet()) {
            Action action = entry.getKey();
            I q = entry.getValue();
            dvalues.put(action, q == null ? 0.0d : q.getValue());
        }

        return dvalues;
    }

    public Set<Action> getAllValidActions(State state) {
        Set<Action> valid = new HashSet<Action>();
        Map<Action, I> actionsValues = getValues(state);

        for (Map.Entry<Action, I> entry : actionsValues.entrySet()) {
            Action action = entry.getKey();
            Integer value = entry.getValue() == null ? 0 : entry.getValue().getFrequency();
            if (value > 0) {
                valid.add(action);
            }
        }
        return valid;
    }

    public String[][] getFrequencyTableStr() {
        String[][] table = new String[getStates().size() + 1][getActions().size() + 1];
        table[0][0] = getTitle();
        int i = 1;
        for (State state : getStates()) {
            table[i++][0] = state.getName();
        }
        int j = 1;
        for (Action action : getActions()) {
            table[0][j++] = action.getName();
        }

        i = 1;
        for (State state : getStates()) {
            j = 1;
            for (Action action : getActions()) {
                table[i][j] = (get(state, action) != null ? get(state, action).getFrequency() : 0) + "";
                j++;
            }
            i++;
        }

        return table;
    }

    public String[][] getFrequencyTableModel() {
        String[][] table = new String[getStates().size()][getStates().size()];
        table[0][0] = getTitle();

        for (State state : getStates()) {
            int i = GridUtils.getRow(state);
            int j = GridUtils.getCol(state);
            String str = "";
            for (Action action : getActions()) {
                str += "" + action.getName().charAt(0) + get(state, action).getFrequency() + " ";
            }

            table[i][j] = str;
        }

        return table;
    }

    public SimplePolicy getSimplePolicy() {
        final SimplePolicy policy = new SimplePolicy();

        for (State state : getStates()) {
            double max = 0;
            Action max_action = null;
            for (Action action : getActions()) {
                Double value = getValue(state, action);
                if (value != 0 && (max_action == null || value > max)) {
                    max = value;
                    max_action = action;
                }
            }
            if (max_action != null) {
                policy.put(state, max_action);
            }
        }

        return policy;
    }

    public Policy getPolicy() {
        final Policy policy = new Policy();

        for (State state : getStates()) {
            Map<Action, Double> map = new HashMap<Action, Double>();
            for (Action action : getActions()) {
                Double value = getValue(state, action);
                map.put(action, value);
            }
            policy.put(state, map);
        }

        return policy;
    }

    public double getTotal(State pState) {
        int count = 0;

        Map<Action, I> v = getValues().get(pState);
        for (Map.Entry<Action, I> entry : v.entrySet()) {
            count += entry.getValue().getFrequency();
        }
        return count;
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : getStates()) {
            double max = -Double.MAX_VALUE;
            for (Action action : getActions()) {
                Double value = getValue(state, action);
                if (value != 0 && value >= max) {
                    max = value;
                }
            }
            map.put(state, max == -Double.MAX_VALUE ? 0 : max);
        }

        return map;
    }

    @Override
    protected String formatValue(State state, Action action, I item) {
        return String.format("%.4g", item == null ? 0.0 : item.getValue());
    }

    public Double getValue(State state, Action action) {
        return get(state, action) == null ? 0.0 : get(state, action).getValue();
    }

    protected Integer incFrequency(State state, Action action) {
        QTableItem item = get(state, action);
        return item != null ? item.getFrequency() + 1 : 1;
    }

    @Override
    public QTable<QTableItem> clone() {
        return new QTable<QTableItem>(this);
    }

    public void updateQ(MDP model, double qValue, State state, Action action, double reward, State nextState) {
        put(state, action, (I) new QTableItem(qValue, reward, incFrequency(state, action), nextState));
    }
}
