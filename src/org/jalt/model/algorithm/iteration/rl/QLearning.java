package org.jalt.model.algorithm.iteration.rl;

import org.jalt.model.action.Action;
import org.jalt.model.model.MDP;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class QLearning<M extends MDP> extends ReinforcementLearning<M> {

	@Override
	public double computeQ(State state, Action action, double reward, State nextState) {
		// get current q value
		double cq = q.getValue(state, action);
		// compute the right side of the equation
		double value = reward + (getGama() * getMax(nextState)) - cq;
		// compute new q value
		double newq = cq + getAlpha() * value;

		return newq;
	}
}
