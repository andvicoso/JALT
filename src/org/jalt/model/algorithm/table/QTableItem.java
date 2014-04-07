package org.jalt.model.algorithm.table;

import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class QTableItem {

    private Double value;
    private Double reward;
    private Integer freq;
    private State finalState;

    public QTableItem() {
        reward = 0d;
        freq = 0;
        value = 0d;
    }

    public QTableItem(Double value, Double reward, Integer freq, State finalState) {
        this.value = value;
        this.reward = reward;
        this.freq = freq;
        this.finalState = finalState;
    }

    public QTableItem(QTableItem item) {
        this.value = item.value;
        this.reward = item.reward;
        this.freq = item.freq;
        this.finalState = item.finalState;
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
