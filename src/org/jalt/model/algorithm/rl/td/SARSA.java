package org.jalt.model.algorithm.rl.td;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class SARSA<M extends MDP> extends QLearning<M> {

	@Override
	public double computeQ(State state, Action action, double reward, State nextState) {
		// get next action
		Action nextAction = q.getBestAction(nextState);// or epsilon-greedy
		// get current q value
		double cq = q.getValue(state, action);
		// get new q value
		double value = reward + (getGama() * getQTable().getValue(nextState, nextAction)) - cq;
		// compute new q value
		double newq = cq + getAlpha() * value;

		return newq;
	}
}
