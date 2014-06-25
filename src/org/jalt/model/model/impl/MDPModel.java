package org.jalt.model.model.impl;

import java.io.Serializable;
import java.util.Collection;

import org.jalt.model.action.Action;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.state.State;

public class MDPModel implements MDP, Serializable {

	private TransitionFunction transitionFunction;
	private RewardFunction rewardFunction;
	private Collection<State> states;
	private Collection<Action> actions;
	private int agents = 1;

	public MDPModel() {
	}

	public MDPModel(TransitionFunction transitionFunction, RewardFunction rewardFunction,
			Collection<State> states, Collection<Action> actions, int agents) {
		this.transitionFunction = transitionFunction;
		this.rewardFunction = rewardFunction;
		this.states = states;
		this.actions = actions;
		this.agents = agents;
	}

	@Override
	public MDPModel copy() {
		return new MDPModel(transitionFunction, rewardFunction, states, actions, agents);
	}

	@Override
	public Collection<State> getStates() {
		return states;
	}

	@Override
	public Collection<Action> getActions() {
		return actions;
	}

	@Override
	public int getAgents() {
		return agents;
	}

	@Override
	public TransitionFunction getTransitionFunction() {
		return transitionFunction;
	}

	@Override
	public void setTransitionFunction(TransitionFunction transitionFunction) {
		this.transitionFunction = transitionFunction;
	}

	@Override
	public RewardFunction getRewardFunction() {
		return rewardFunction;
	}

	@Override
	public void setRewardFunction(RewardFunction rewardFunction) {
		this.rewardFunction = rewardFunction;
	}

	@Override
	public void setAgents(int agents) {
		this.agents = agents;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getStates().size() < Problem.MAX_SIZE_PRINT)
			sb.append("\nStates: ").append(states);
		sb.append("\nActions: ").append(actions);
		sb.append("\nAgents: ").append(agents);
		// GridPrinter gp = new GridPrinter();
		// TODO:
		// sb.append("\nReward function: ").append("\n").append(gp.print(getRewardFunction(),
		// this));
		// TODO:
		// sb.append("\nTransition function: ").append("\n").append(gp.print(getTransitionFunction(),
		// this));

		return sb.toString();
	}

	@Override
	public void setActions(Collection<Action> pActions) {
		actions = pActions;
	}

	@Override
	public void setStates(Collection<State> states) {
		this.states = states;
	}
}
