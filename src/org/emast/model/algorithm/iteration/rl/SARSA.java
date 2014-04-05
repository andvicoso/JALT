package org.emast.model.algorithm.iteration.rl;

import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author andvicoso
 */
public class SARSA<M extends MDP> extends QLearning<M> {

    @Override
    public double computeQ(State state, Action action, double reward, State nextState) {
        Policy p = q.getPolicy(true);
        //get next action
        Action nextAction = p.getBestAction(nextState);//or epsilon-greedy
        //get current q value
        double cq = getQTable().getValue(state, action);
        //get new q value
        double value = reward + (getGama() * getQTable().getValue(nextState, nextAction)) - cq;
        double newq = cq + getAlpha() * value;
        //save q
        return newq;
    }
}
