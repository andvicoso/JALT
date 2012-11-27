package org.emast.model.solution;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

public class Policy extends HashMap<State, Map<Action, Double>> {

    public Set<State> getStates() {
        return keySet();
    }

    @Override
    public String toString() {
        final List<State> list = new ArrayList<State>(keySet());
        Collections.sort(list);

        final StringBuilder sb = new StringBuilder();
        for (final State state : list) {
            final Map<Action, Double> actions = get(state);
            sb.append("(");
            sb.append(state.getName());
            sb.append(", ");
            sb.append(actions);
            sb.append(")\n");
        }

        return sb.toString();
    }

    public Action getBest(State state) {
        Map<Action, Double> map = get(state);
        if (map != null && !map.isEmpty()) {
            Double max = Collections.max(map.values());
            Collection<Action> bestActions = CollectionsUtils.getKeysForValue(map, max);
            return CollectionsUtils.getRandom(bestActions);
        }
        return null;
    }

    public void put(State state, Action action, Double value) {
        if (!containsKey(state)) {
            put(state, new HashMap<Action, Double>());
        }
        Map<Action, Double> map = get(state);
        map.put(action, value);
    }

    public Map<State, Action> getBestPolicy() {
        final Map<State, Action> policy = new HashMap<State, Action>();

        for (final State state : keySet()) {
            policy.put(state, getBest(state));
        }

        return policy;
    }
}
