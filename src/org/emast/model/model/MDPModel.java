package org.emast.model.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.agent.Agent;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.state.State;

public abstract class MDPModel implements MDP, Serializable {

    private Collection<State> states;
    private Collection<Action> actions;
    private List<Agent> agents;

    public MDPModel(final Collection<State> pStates,
            final Collection<Action> pActions,
            final List<Agent> pAgents) {
        states = pStates;
        actions = pActions;
        agents = pAgents;
    }

    public MDPModel(final MDPModel pModel) {
        this(pModel.getStates(), pModel.getActions(), pModel.getAgents());
    }

    @Override
    public MDPModel copy() {
        return new MDPModel(states, actions, agents) {
            @Override
            public TransitionFunction getTransitionFunction() {
                return MDPModel.this.getTransitionFunction();
            }

            @Override
            public RewardFunction getRewardFunction() {
                return MDPModel.this.getRewardFunction();
            }
        };
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

    public Agent getAgent(final String pName) {
        for (final Agent agent : getAgents()) {
            if (agent.getName().equals(pName)) {
                return agent;
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
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public abstract TransitionFunction getTransitionFunction();

    @Override
    public abstract RewardFunction getRewardFunction();

//    public void setActions(final Collection<Action> actions) {
//        this.actions = actions;
//    }
//
//    public void setAgents(int pNumOfAgents) {
//        setAgents(ModelUtils.createList(Agent.class, pNumOfAgents));
//    }
//
//    public void setAgents(final Collection<Agent> agents) {
//        this.agents = agents;
//    }
//
//    public void setStates(final Collection<State> states) {
//        this.states = states;
//    }
//
//    public void setStates(final State... states) {
//        setStates(Arrays.asList(states));
//    }
//
//    public void setAgents(final Agent... pAgents) {
//        setAgents(Arrays.asList(pAgents));
//    }
//
//    public void setActions(final Action... pActions) {
//        setActions(Arrays.asList(pActions));
//    }
    public Collection<State> getStates(final String... pStatesStrs) {
        final Collection<State> sts = new ArrayList<State>(pStatesStrs.length);
        for (final String strState : pStatesStrs) {
            sts.add(getState(strState));
        }

        return sts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Model: \n");
        sb.append("States: ").append(states).append("\n");
        sb.append("Actions: ").append(actions).append("\n");
        sb.append("Agents: ").append(agents).append("\n");
        sb.append("Reward function: ").append(getRewardFunction()).append("\n");
        sb.append("Transition function: ").append(getTransitionFunction()).append("\n");

        return sb.toString();
    }
}
