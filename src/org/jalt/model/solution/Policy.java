package org.jalt.model.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.erg.ERGLearningUtils;

public class Policy extends HashMap<State, Map<Action, Double>> {

	private static final int DECIMAL_ACTION = 3;
	private static final String FORMAT = "%1$." + DECIMAL_ACTION + "f";

	public Policy() {
	}

	public Set<State> getStates() {
		return keySet();
	}

	@Override
	public String toString() {
		final List<State> list = new ArrayList<>(keySet());
		Collections.sort(list);

		final StringBuilder sb = new StringBuilder();
		for (final State state : list) {
			final Map<Action, Double> actions = get(state);
			sb.append("(");
			sb.append(state.getName());
			sb.append(", ");
			sb.append(getBestActions(state));
			sb.append(", ");
			sb.append(toString(actions));
			sb.append(")\n");
		}

		return sb.toString();
	}

	public Map<Action, Double> getBestMapActions(State state) {
		Map<Action, Double> map = get(state);
		if (map != null && !map.isEmpty()) {
			Map<Action, Double> temp = new HashMap<>(map);
			Double max = Collections.max(temp.values());
			Set<Action> actions = CollectionsUtils.getKeysForValue(temp, max);
			if (max == 0) {
				for (Action action : actions) {
					temp.remove(action);
				}
				if (temp.isEmpty()) {
					return Collections.emptyMap();
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
		return Collections.emptyMap();
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
					return Collections.emptySet();
				}
				max = Collections.max(temp.values());
				actions = CollectionsUtils.getKeysForValue(temp, max);
			}

			return actions;
		}
		return Collections.emptySet();
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

	public Policy optimize() {
		final Policy policy = new Policy();

		for (final State state : keySet()) {
			policy.put(state, getBestMapActions(state));
		}

		return policy;
	}

	public Policy optimize(ERGQTable q) {
		Policy single = new Policy();
		for (Map.Entry<State, Map<Action, Double>> entry : entrySet()) {
			State state = entry.getKey();
			Action bestAction = ERGLearningUtils.getBestAction(entry.getValue(), q.getDoubleValues(state));
			single.put(state, Collections.singletonMap(bestAction, 1d));
		}

		return single;
	}

	public void join(Policy policy) {
		for (State state : policy.keySet()) {
			if (containsKey(state)) {
				Map<Action, Double> pActions = policy.get(state);
				Map<Action, Double> actions = get(state);
				for (Action action : pActions.keySet()) {
					Double value = pActions.get(action);
					if (actions.containsKey(action))
						value = (actions.get(action) + value) / 2;
					actions.put(action, value);
				}
			} else {
				put(state, policy.get(state));
			}
		}
	}
}
