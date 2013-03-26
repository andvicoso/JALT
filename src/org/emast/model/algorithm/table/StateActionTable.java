package org.emast.model.algorithm.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class StateActionTable<T> {

    protected Map<State, Map<Action, T>> values;

    public StateActionTable(Collection<State> states, Collection<Action> actions, T initialValue) {
        values = new HashMap<State, Map<Action, T>>(states.size());

        for (State state : states) {
            Map<Action, T> actionMap = new HashMap<Action, T>(actions.size());
            values.put(state, actionMap);

            for (Action action : actions) {
                actionMap.put(action, initialValue);
            }
        }
    }

    public StateActionTable(Collection<State> states, Collection<Action> actions) {
        this(states, actions, null);
    }

    public StateActionTable(StateActionTable<T> q) {
        values = new HashMap<State, Map<Action, T>>(q.getStates().size());

        for (State state : q.getStates()) {
            Map<Action, T> actionMap = new HashMap<Action, T>(q.getActions().size());
            values.put(state, actionMap);

            for (Action action : q.getActions()) {
                actionMap.put(action, q.get(state, action));
            }
        }
    }

    public T get(State state, Action action) {
        return values.get(state).get(action);
    }

    public void put(State state, Action action, T value) {
        values.get(state).put(action, value);
    }

    public Collection<Action> getActions() {
        return values.values().iterator().next().keySet();
    }

    public Collection<State> getStates() {
        return values.keySet();
    }

    public Map<State, Map<Action, T>> getValues() {
        return values;
    }

    public String[][] toTable() {
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
                table[i][j] = getValueString(state, action);
                j++;
            }
            i++;
        }

        return table;
    }

    protected String getTitle() {
        return "";
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable());
    }

    protected String formatValue(State state, Action action, T value) {
        return value.toString();
    }

    private String getValueString(State state, Action action) {
        T value = get(state, action);
        return formatValue(state, action, value);
    }
}
