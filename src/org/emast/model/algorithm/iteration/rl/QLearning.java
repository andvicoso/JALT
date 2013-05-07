package org.emast.model.algorithm.iteration.rl;

import org.emast.model.action.Action;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class QLearning<M extends MDP> extends AbstractQLearning<M> {

    public QLearning(QTable q) {
        super(q);
    }

    public QLearning() {
    }

    @Override
    public double computeQ(State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = q.getValue(state, action);
        //compute the right side of the equation
        double value = reward + (getGama() * getMax(nextState)) - cq;
        //compute new q value
        double newq = cq + getAlpha() * value;

        return newq;
    }
}
