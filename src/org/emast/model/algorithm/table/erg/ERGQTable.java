package org.emast.model.algorithm.table.erg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.table.StateActionTable;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SimplePolicy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class ERGQTable extends StateActionTable<ERGQTableItem> {

    private Map<Expression, Double> expSum;
    private Map<Expression, Integer> expCount;

    public ERGQTable(Collection<State> states, Collection<Action> actions) {
        super(states, actions, new ERGQTableItem());
        initExpMaps();
    }

    public ERGQTable(ERGQTable q) {
        super(q);
        initExpMaps();
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
                table[i][j] = get(state, action).getFrequency() + "";
                j++;
            }
            i++;
        }

        return table;
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : getStates()) {
            double max = -Double.MAX_VALUE;
            for (Action action : getActions()) {
                Double value = get(state, action).getValue();
                if (value != 0 && value >= max) {
                    max = value;
                }
            }
            map.put(state, max == -Double.MAX_VALUE ? 0 : max);
        }

        return map;
    }

    public SimplePolicy getSimplePolicy() {
        final SimplePolicy policy = new SimplePolicy();

        for (State state : getStates()) {
            double max = 0;
            Action max_action = null;
            for (Action action : getActions()) {
                Double value = get(state, action).getValue();
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
                Double value = get(state, action).getValue();
                map.put(action, value);
            }
            policy.put(state, map);
        }

        return policy;
    }

    public double getTotal(State pState) {
        int count = 0;

        Map<Action, ERGQTableItem> v = getValues().get(pState);
        for (Map.Entry<Action, ERGQTableItem> entry : v.entrySet()) {
            count += entry.getValue().getFrequency();
        }
        return count;
    }

    public Map<Expression, Double> getExpsValues() {
        Map<Expression, Double> expValues = new HashMap<Expression, Double>();

        for (Expression p : expSum.keySet()) {
            double value = 0;
            Double sum = expSum.get(p);
            Integer count = expCount.get(p);
            if (sum != null && count != null) {
                value = sum / count;
            }
            expValues.put(p, value);
        }

        return expValues;
    }

    public Expression getBadExpression() {
        Map<Expression, Double> expValues = getExpsValues();

        for (Map.Entry<Expression, Double> entry : expValues.entrySet()) {
            Expression proposition = entry.getKey();
            Double value = entry.getValue();
            if (value < BAD_EXP_VALUE) {
                return proposition;
            }
        }

        return null;
    }

    @Override
    public void put(State state, Action action, ERGQTableItem value) {
        super.put(state, action, value);
        updateProps(value.getValue(), value.getExpression());
    }

    protected void updateProps(double value, Expression exp) {
        if (exp != null) {
            double sum = 0;
            int count = 0;

            if (expSum.containsKey(exp)) {
                sum = expSum.get(exp);
            }
            if (expCount.containsKey(exp)) {
                count = expCount.get(exp);
            }

            expSum.put(exp, sum + value);
            expCount.put(exp, count + 1);
        }
    }

    private void initExpMaps() {
        expCount = new HashMap<Expression, Integer>();
        expSum = new HashMap<Expression, Double>();
    }

    @Override
    protected String formatValue(State state, Action action, ERGQTableItem value) {
        return String.format("%.4g", value.getValue());
    }
}
