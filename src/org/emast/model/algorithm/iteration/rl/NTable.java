package org.emast.model.algorithm.iteration.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class NTable {

    protected List<State> states;
    protected List<Action> actions;
    protected Double[][] values;

    public NTable(NTable q) {
        this(q.getStates(), q.getActions());

        for (int i = 0; i < states.size(); i++) {
            System.arraycopy(q.getValues()[i], 0, values[i], 0, actions.size());
        }
    }

    public NTable(List<State> pStates, List<Action> pActions) {
        states = pStates;
        actions = pActions;
        values = new Double[states.size()][actions.size()];

        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < actions.size(); j++) {
                values[i][j] = 0d;
            }
        }
    }

    public NTable(Collection<State> states, Collection<Action> actions) {
        this(new ArrayList<State>(states), new ArrayList<Action>(actions));
    }

    public Double get(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return si >= 0 && ai >= 0 ? values[si][ai] : 0d;
    }

    public void put(State state, Action action, Double value) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);
        values[si][ai] = value;
    }
    
    void put(State state, Action action, double newq, double reward, State nextState) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Double[][] getValues() {
        return values;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<State> getStates() {
        return states;
    }

    public String[][] toTable() {
        String[][] table = new String[states.size() + 1][actions.size() + 1];
        table[0][0] = getTitle();
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
                table[i][j] = String.format("%.4g", value);
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
}
