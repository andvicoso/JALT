package org.emast.model.algorithm.iteration.rl;

import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class FrequencyTable extends NTable {

    public FrequencyTable(NTable q) {
        super(q);
    }

    public FrequencyTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions);
    }

    public FrequencyTable(List<State> pStates, List<Action> pActions) {
        super(pStates, pActions);
    }

    public void inc(State state, Action action) {
        put(state, action, get(state, action) + 1);
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable(false));
    }

    public double getTotal(State pState) {
        int count = 0;
        int si = states.indexOf(pState);
        for (int i = 0; i < values[si].length; i++) {
            count += values[si][i];
        }
        return count;
    }

    public String[][] toTable(boolean pShowPercent) {
        String[][] table = new String[states.size() + 1][actions.size() + 1];
        table[0][0] = "f/to";
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
                Double count = get(state, action);
                String value = count + "";
                if (pShowPercent) {
                    double total = getTotal(state);
                    if (total != 0) {
                        double percent = count / total * 100;
                        value = String.format("%.4g", percent);
                    }
                }
                table[i][j] = value;
                j++;
            }
            i++;
        }

        return table;
    }
}
