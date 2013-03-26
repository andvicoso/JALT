package org.emast.model.algorithm.table;

import java.util.Collection;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class FrequencyTable extends DoubleTable {

    private static final boolean showPercent = false;

    public FrequencyTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions);
    }

    public void inc(State state, Action action) {
        put(state, action, get(state, action) + 1);
    }

    public double getTotal(State pState) {
        int count = 0;

        Map<Action, Double> v = getValues().get(pState);
        for (Map.Entry<Action, Double> entry : v.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    @Override
    protected String getTitle() {
        return "f/to";
    }

    @Override
    protected String formatValue(State state, Action action, Double count) {
        String value = count + "";
        if (showPercent) {
            double total = getTotal(state);
            if (total != 0) {
                double percent = count / total * 100;
                value = formatValue(state, action, percent);
            }
        }
        return value;
    }
}
