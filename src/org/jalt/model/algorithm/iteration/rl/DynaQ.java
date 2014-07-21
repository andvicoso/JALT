package org.jalt.model.algorithm.iteration.rl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.actionchooser.EpsilonGreedy;
import org.jalt.model.algorithm.table.QTableItem;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 * 
 * @author andvicoso
 */
public class DynaQ<M extends MDP> extends QLearning<M> {

	private double EPSILON = 0.1;
	private int n = 5;
	private Map<State, Set<Action>> visited;

	@Override
	protected void init(Problem<M> pProblem, Map<String, Object> pParameters) {
		super.init(pProblem, pParameters);
		setActionChooser(new EpsilonGreedy<Action>(EPSILON));
		visited = new HashMap<State, Set<Action>>();
	}

	@Override
	protected void updateQ(State state, Action action, double reward, State nextState) {
		if (!visited.containsKey(state))
			visited.put(state, new HashSet<Action>());
		visited.get(state).add(action);

		super.updateQ(state, action, reward, nextState);
		if (episodes > 0) {
			plan();
		}
	}

	// @Override
	// protected Map<Action, Double> getActionValues(State pState) {
	// return episodes > 0 ? q.getDoubleValues(pState) :
	// super.getActionValues(pState);
	// }

	private void plan() {
		for (int i = 0; i < n; i++) {
			State nextState = CollectionsUtils.getRandom(visited.keySet());
			Action nextAction = CollectionsUtils.getRandom(visited.get(nextState));
			QTableItem item = q.get(nextState, nextAction);
			State finalState = item.getFinalState();
			double reward = item.getReward();
			super.updateQ(nextState, nextAction, reward, finalState);
		}
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getN() {
		return n;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder(super.printResults());
		sb.append("\nN: ").append(n);
		sb.append("\nEpsilon: ").append(EPSILON);

		return sb.toString();
	}
}
