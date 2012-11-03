package org.emast.model.model.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

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

    public Action getAction(final String pName) {
        if (pName.equals(Action.ANY.getName())) {
            return Action.ANY;
        }
        for (final Action action : getActions()) {
            if (action.getName().equals(pName)) {
                return action;
            }
        }

        return null;
    }

    public State getState(final String pName) {
        if (pName.equals(State.ANY.getName())) {
            return State.ANY;
        }
        for (final State state : getStates()) {
            if (state.getName().equals(pName)) {
                return state;
            }
        }
        return null;
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

    public Collection<State> getStates(final String... pStatesStrs) {
        final Collection<State> sts = new ArrayList<State>(pStatesStrs.length);
        for (final String strState : pStatesStrs) {
            sts.add(getState(strState));
        }

        return sts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nStates: ").append(states);
        sb.append("\nActions: ").append(actions);
        sb.append("\nAgents: ").append(agents);
        sb.append("\nReward function: ").append(getRewardFunction());
        sb.append("\nTransition function: ").append(getTransitionFunction());

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
