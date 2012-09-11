package org.emast.model.comm;

import org.emast.model.agent.Agent;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class StateRewardMessage extends Message<Double> {

    private State state;

    public StateRewardMessage(final State pState, final double pReward,
            final Agent sender) {
        super(pReward, sender);
        state = pState;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "(" + getValue() + ", " + getValue() + ")";
    }
}
