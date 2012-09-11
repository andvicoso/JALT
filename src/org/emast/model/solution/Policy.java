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
            sb.append("(").append(state.getName());
            sb.append(", ").append(action.getName()).append(")");
            //if (list.indexOf(state) > 0 && list.indexOf(state) % 10 == 0)
            //result += "\n";
        }

        String result = sb.toString();
        if (result.length() > 1 && result.lastIndexOf(',') > -1) {
            result = result.substring(0, result.lastIndexOf(','));
        }

        return result;
    }
}
