package org.emast.model.solution;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.state.State;

public class SimplePolicy extends HashMap<State, Action> {

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
}
