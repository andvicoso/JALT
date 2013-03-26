package org.emast.model.algorithm.iteration.rl;

import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class SARSA<M extends MDP> extends QLearning<M> {

    @Override
    protected void updateQTable(State state, Action action, double reward, State nextState) {
        //get next action
        Action nextAction = getQTable().getPolicy(true).getBestAction(nextState);//model.getTransitionFunction().getAction(model.getActions(), state);
        //get current q value
        double cq = getQTable().get(state, action);
        //get new q value
        double value = reward + (getGama() * getQTable().get(nextState, nextAction)) - cq;
        double newq = cq + getAlpha() * value;
        //save q
        getQTable().put(state, action, newq);
    }
}
