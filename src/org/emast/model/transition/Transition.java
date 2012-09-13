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

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transition other = (Transition) obj;
        if (this.state != other.state && (this.state == null || !this.state.equals(other.state))) {
            return false;
        }
        if (this.action != other.action && (this.action == null || !this.action.equals(other.action))) {
            return false;
        }
        return true;
    }
}
