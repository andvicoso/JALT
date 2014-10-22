package org.jalt.model.solution;

import java.util.*;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;

public class SinglePolicy extends HashMap<State, Action> {

    @Override
    public String toString() {
        final List<State> list = new ArrayList<State>(keySet());
        Collections.sort(list);

        final StringBuilder sb = new StringBuilder();
        for (final State state : list) {
            final Action action = get(state);
            sb.append("(");
            sb.append(state.getName());
            sb.append(", ");
            sb.append(action);
            sb.append(")\n");
        }

        return sb.toString();
    }

    public Set<State> getStates() {
        return keySet();
    }
}
