package org.emast.model.model;

import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.state.State;

public interface MDP {

    MDP copy();

    Collection<State> getStates();

    Collection<Action> getActions();

    int getAgents();

    TransitionFunction getTransitionFunction();

    RewardFunction getRewardFunction();
}
