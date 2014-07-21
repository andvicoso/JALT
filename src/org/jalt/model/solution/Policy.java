package org.jalt.model.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;

public class Policy extends TreeMap<State, Action> {
	private static final int DECIMAL_ACTION = 3;
	private static final String FORMAT = "%1$." + DECIMAL_ACTION + "f";

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
}
