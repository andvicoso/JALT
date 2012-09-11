package org.emast.model.model;

import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
import org.emast.model.agent.Agent;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.state.State;

public interface MDP {

    <M extends MDP> M copy();

    Collection<State> getStates();

    Collection<Action> getActions();

    List<Agent> getAgents();

    TransitionFunction getTransitionFunction();

    RewardFunction getRewardFunction();
}
