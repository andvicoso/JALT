package org.emast.model.model;

import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.state.State;

/**
 * 
 * @author andvicoso
 */
public interface MDP {

    MDP copy();

    Collection<State> getStates();

    void setStates(Collection<State> states);

    Collection<Action> getActions();

    void setActions(Collection<Action> actions);

    int getAgents();

    void setAgents(int pAgents);
    
    TransitionFunction getTransitionFunction();

    RewardFunction getRewardFunction();

    void setTransitionFunction(TransitionFunction tf);

    void setRewardFunction(RewardFunction rf);
}
