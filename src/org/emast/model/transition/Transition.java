package org.emast.model.transition;

import org.emast.model.action.Action;
import org.emast.model.state.State;

public class Transition {

    private State state;
    private Action action;

    public Transition(final State pState, final Action pAction) {
        state = pState;
        action = pAction;
    }

    public State getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "(" + state.getName() + ", " + action.getName() + ")";
    }
}
