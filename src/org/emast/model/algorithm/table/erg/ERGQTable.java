package org.emast.model.algorithm.table.erg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGQTable extends QTable<ERGQTableItem> {

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

    @Override
    public void put(State state, Action action, ERGQTableItem value) {
        super.put(state, action, value);
        updateExpressionValues(value.getValue(), value.getExpression());
    }

    protected void updateExpressionValues(double value, Expression exp) {
        if (exp != null && !exp.isEmpty()) {
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
    public QTable<ERGQTableItem> clone() {
        return new ERGQTable(this);
    }

    @Override
    public void updateQ(MDP model, double qValue, State state, Action action, double reward, State nextState) {
        final ERG erg = (ERG) model;
        //get expression for next state
        Expression exp = erg.getPropositionFunction().getExpressionForState(nextState);
        //save q
        put(state, action, new ERGQTableItem(qValue, reward, incFrequency(state, action), nextState, exp));
        //update
        updateExpressionValues(reward, exp);
    }
}
