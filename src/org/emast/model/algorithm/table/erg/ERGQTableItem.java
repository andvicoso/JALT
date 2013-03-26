package org.emast.model.algorithm.table.erg;

import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGQTableItem {

    private Double value;
    private Double reward;
    private Integer freq;
    private State finalState;
    private Expression expression;

    public ERGQTableItem() {
        reward = 0d;
        freq = 0;
        value = 0d;
    }

    public ERGQTableItem(Double value, Double reward, Integer freq, State finalState, Expression expression) {
        this.value = value;
        this.reward = reward;
        this.freq = freq;
        this.finalState = finalState;
        this.expression = expression;
    }

    public ERGQTableItem(ERGQTableItem item) {
        this.value = item.value;
        this.reward = item.reward;
        this.freq = item.freq;
        this.finalState = item.finalState;
        this.expression = item.expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public Integer getFrequency() {
        return freq;
    }

    public void setFrequency(Integer freq) {
        this.freq = freq;
    }

    public State getFinalState() {
        return finalState;
    }

    public void setFinalState(State finalState) {
        this.finalState = finalState;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
