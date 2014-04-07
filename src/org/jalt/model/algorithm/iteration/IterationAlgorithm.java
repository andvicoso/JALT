package org.jalt.model.algorithm.iteration;

import static org.jalt.util.DefaultTestProperties.GAMA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public abstract class IterationAlgorithm<M extends MDP, R> implements Algorithm<M, R> {

	/**
	 * Discount factor The discount factor determines the importance of future
	 * rewards. A factor of 0 will make the agent "opportunistic" by only
	 * considering current rewards, while a factor approaching 1 will make it
	 * strive for a long-term high reward. If the discount factor meets or
	 * exceeds 1, the values may diverge.
	 */
	protected double gama = GAMA;
	protected int episodes = 0;
	protected M model;

	public int getIterations() {
		return episodes;
	}

	public double getGama() {
		return gama;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nEpisodes: ").append(episodes);
		// sb.append("\nGama: ").append(gama);//TODO:descomentar em producao

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	protected Map<Action, Double> getQ(MDP pModel, State pState, Map<State, Double> v) {
		return getQ(pModel.getTransitionFunction(), pModel.getRewardFunction(),
				pModel.getActions(), pModel.getStates(), pState, v);
	}

	protected Map<Action, Double> getQ(TransitionFunction tf, RewardFunction rf,
			Collection<Action> actions, Collection<State> states, State state, Map<State, Double> v) {
		Map<Action, Double> q = new HashMap<Action, Double>();
		// search in all actions and discover if it is possible
		// or not through the transition function
		for (Action action : actions) {
			double value = getValue(model, state, action, v);
			q.put(action, value);
		}
		return q;
	}

	protected double getValue(MDP model, State state, Action action, Map<State, Double> v) {
		if (action != null) {
			double reward = model.getRewardFunction().getValue(state, action);
			double value = reward
					+ (getGama() * getSum(model.getTransitionFunction(), model.getStates(), state,
							action, v));
			return value;
		}

		return 0;
	}

	protected double getSum(TransitionFunction tf, Collection<State> states, State pState,
			Action pAction, Map<State, Double> v) {
		double sum = 0;

		for (State finalState : states) {
			Double trans = tf.getValue(pState, finalState, pAction);
			sum += trans * getVValue(v, finalState);
		}

		return sum;
	}

	private double getVValue(Map<State, Double> v, State state) {
		return v.isEmpty() || !v.containsKey(state) ? 0 : v.get(state);
	}

	protected void initializeV(Problem<M> pProblem, Map<State, Double> v) {
		for (State state : pProblem.getModel().getStates()) {
			v.put(state, 0.0);
		}
	}
}