package org.emast.model.solution;

import java.util.*;
import java.util.Map.Entry;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

public class Policy extends HashMap<State, Map<Action, Double>> {

    private static final int DECIMAL_ACTION = 3;
    private static final String FORMAT = "%1$." + DECIMAL_ACTION + "f";

    public Policy(SinglePolicy single) {
        for (Entry<State, Action> entry : single.entrySet()) {
            State state = entry.getKey();
            Action action = entry.getValue();
            put(state, Collections.singletonMap(action, 1d));
        }
    }

    public Policy() {
    }

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
            sb.append(toString(actions));
            sb.append(")\n");
        }

        return sb.toString();
    }

    public Map<Action, Double> getBestMapActions(State state) {
        Map<Action, Double> map = get(state);
        if (map != null && !map.isEmpty()) {
            Map<Action, Double> temp = new HashMap<Action, Double>(map);
            Double max = Collections.max(temp.values());
            Set<Action> actions = CollectionsUtils.getKeysForValue(temp, max);
            if (max == 0) {
                for (Action action : actions) {
                    temp.remove(action);
                }
                if (temp.isEmpty()) {
                    return Collections.EMPTY_MAP;
                }
                max = Collections.max(temp.values());
                actions = CollectionsUtils.getKeysForValue(temp, max);
            }

            Map<Action, Double> result = new HashMap<Action, Double>();
            for (Action action : actions) {
                result.put(action, max);
            }

            return result;
        }
        return Collections.EMPTY_MAP;
    }

    public Collection<Action> getBestActions(State state) {
        Map<Action, Double> map = get(state);
        if (map != null && !map.isEmpty()) {
            Map<Action, Double> temp = new HashMap<Action, Double>(map);
            Double max = Collections.max(temp.values());
            Set<Action> actions = CollectionsUtils.getKeysForValue(temp, max);
            if (max == 0) {
                for (Action action : actions) {
                    temp.remove(action);
                }
                if (temp.isEmpty()) {
                    return Collections.EMPTY_SET;
                }
                max = Collections.max(temp.values());
                actions = CollectionsUtils.getKeysForValue(temp, max);
            }

            return actions;
        }
        return Collections.EMPTY_SET;
    }

    public Action getBestAction(State state) {
        Map<Action, Double> map = get(state);
        if (map != null && !map.isEmpty()) {
            Double max = Collections.max(map.values());
            Map<Action, Double> temp = new HashMap<Action, Double>(map);
            Set<Action> actions = CollectionsUtils.getKeysForValue(temp, max);
            if (max == 0) {
                for (Action action : actions) {
                    temp.remove(action);
                }
                if (temp.isEmpty()) {
                    return null;
                }
                max = Collections.max(temp.values());
                actions = CollectionsUtils.getKeysForValue(temp, max);
            }
            return CollectionsUtils.getRandom(actions);
        }
        return null;
    }

    public Double getBestValue(State state) {
        Map<Action, Double> map = get(state);
        if (map != null && !map.isEmpty()) {
            return Collections.max(map.values());
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

    public String toString(Map<Action, Double> map) {
        Iterator<Entry<Action, Double>> i = map.entrySet().iterator();

        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (;;) {
            Entry<Action, Double> e = i.next();
            Action key = e.getKey();
            Double value = e.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(String.format(FORMAT, value));

            if (!i.hasNext()) {
                return sb.append("}").toString();
            }
            sb.append(", ");
        }
    }

    public Map<State, Double> getBestPolicyValue() {
        final Map<State, Double> values = new HashMap<State, Double>();

        for (final State state : keySet()) {
            values.put(state, getBestValue(state));
        }

        return values;
    }

    public SinglePolicy getBestPolicy() {
        final SinglePolicy policy = new SinglePolicy();

        for (final State state : keySet()) {
            policy.put(state, getBestAction(state));
        }

        return policy;
    }

    public Policy optimize() {
        final Policy policy = new Policy();

        for (final State state : keySet()) {
            policy.put(state, getBestMapActions(state));
        }

        return policy;
    }
}
