package org.emast.model.solution;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.state.State;

public class Policy extends HashMap<State, Action> {

    public Set<State> getStates() {
        return keySet();
    }

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
            sb.append(action.getName());
            sb.append(")");
        }

        return sb.toString();
    }
}
