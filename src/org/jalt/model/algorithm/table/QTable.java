package org.jalt.model.algorithm.table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.solution.Policy;
import org.jalt.model.solution.SinglePolicy;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridPrinter;

/**
 * 
 * @author andvicoso
 */
public class QTable<I extends QTableItem> extends StateActionTable<I> {
	public static final String NAME = "qtable";

	public QTable(Collection<State> states, Collection<Action> actions, I initialValue) {
		super(states, actions, initialValue);
	}

	public QTable(Collection<State> states, Collection<Action> actions) {
		super(states, actions);
	}

	public QTable(QTable<I> q) {
		super(q);
	}

	@Override
	public String toString() {
		return new GridPrinter().toTable(toTable());
	}

	public Policy getPolicy(boolean pAddZeros) {
		final Policy policy = new Policy();

		for (State state : getStates()) {
			for (Action action : getActions()) {
				double value = getValue(state, action);
				if (pAddZeros || value != 0) {
					policy.put(state, action, value);
				}
			}
		}

		return policy;
	}

	public Policy getPolicy() {
		return getPolicy(true);
	}

	public Map<Action, Double> getDoubleValues(State state) {
		Map<Action, I> qvalues = getValues(state);
		Map<Action, Double> dvalues = new HashMap<Action, Double>(qvalues.size());
		for (Map.Entry<Action, I> entry : qvalues.entrySet()) {
			Action action = entry.getKey();
			I q = entry.getValue();
			dvalues.put(action, q == null ? 0.0d : q.getValue());
		}

		return dvalues;
	}

	public String[][] getFrequencyTableStr() {
		String[][] table = new String[getStates().size() + 1][getActions().size() + 1];
		table[0][0] = getTitle();
		int i = 1;
		for (State state : getStates()) {
			table[i++][0] = state.getName();
		}
		int j = 1;
		for (Action action : getActions()) {
			table[0][j++] = action.getName();
		}

		i = 1;
		for (State state : getStates()) {
			j = 1;
			for (Action action : getActions()) {
				table[i][j] = (get(state, action) != null ? get(state, action).getFrequency() : 0)
						+ "";
				j++;
			}
			i++;
		}

		return table;
	}

	public Map<State, Double> getFrequencyValues() {
		Map<State, Double> freq = new HashMap<State, Double>(getStates().size());
		for (State state : getStates()) {
			for (Action action : getActions()) {
				QTableItem item = get(state, action);

				if (item.getFinalState() != null) {

					Double f = freq.get(state);
					Double totalf = f == null ? 0 : f;

					f = item.getFrequency() * 1d;
					totalf += f == null ? 0 : f;

					freq.put(item.getFinalState(), totalf);
				}
			}
		}

		return freq;
	}

	public SinglePolicy getSimplePolicy() {
		final SinglePolicy policy = new SinglePolicy();

		for (State state : getStates()) {
			double max = 0;
			Action max_action = null;
			for (Action action : getActions()) {
				Double value = getValue(state, action);
				if (value != 0 && (max_action == null || value > max)) {
					max = value;
					max_action = action;
				}
			}
			if (max_action != null) {
				policy.put(state, max_action);
			}
		}

		return policy;
	}

	public double getTotal(State pState) {
		int count = 0;

		Map<Action, I> v = getValues().get(pState);
		for (Map.Entry<Action, I> entry : v.entrySet()) {
			if (entry.getValue() != null)
				count += entry.getValue().getFrequency();
		}
		return count;
	}

	public Map<State, Double> getStateValue() {
		final Map<State, Double> map = new HashMap<State, Double>();

		for (State state : getStates()) {
			double max = -Double.MAX_VALUE;
			for (Action action : getActions()) {
				Double value = getValue(state, action);
				if (value != 0 && value >= max) {
					max = value;
				}
			}
			map.put(state, max == -Double.MAX_VALUE ? 0 : max);
		}

		return map;
	}

	@Override
	protected String formatValue(State state, Action action, I item) {
		return String.format("%.9g", item == null ? 0.0 : item.getValue());
	}

	public Double getValue(State state, Action action) {
		return get(state, action) == null ? 0.0 : get(state, action).getValue();
	}

	protected Integer incFrequency(State state, Action action) {
		QTableItem item = get(state, action);
		return item != null ? item.getFrequency() + 1 : 1;
	}

	@Override
	public QTable<I> clone() {
		return new QTable<I>(this);
	}

	@SuppressWarnings("unchecked")
	public void updateQ(MDP model, double qValue, State state, Action action, double reward,
			State nextState) {
		put(state, action, (I) new QTableItem(qValue, reward, incFrequency(state, action),
				nextState));
	}

	public String toString(MDP model) {
		return new GridPrinter().toGrid(model, getStateValue());
	}

	public Action getBestAction(State state) {
		Action best = null;
		Double max = -Double.MAX_VALUE;
		for (Action action : getActions()) {
			double value = getValue(state, action);
			if (value > max) {
				max = value;
				best = action;
			}
		}

		return best;
	}
}
