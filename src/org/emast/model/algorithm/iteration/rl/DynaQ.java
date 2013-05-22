package org.emast.model.algorithm.iteration.rl;

import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.actionchooser.EGreedy;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class DynaQ<M extends MDP> extends QLearning<M> {

    private double EPSILON = 0.1;
    private int n = 5;

    public DynaQ() {
    }

    @Override
    protected void init(Problem<M> pProblem, Object... pParameters) {
        super.init(pProblem, pParameters);
        //setActionChooser(new EGreedy(EPSILON));
    }

    @Override
    protected void updateQ(State state, Action action, double reward, State nextState) {
        super.updateQ(state, action, reward, nextState);
        if (episodes > 0) {
            planning();
        }
    }

    @Override
    protected Map<Action, Double> getActionValues(State pState) {
        if (episodes > 0) {
            return q.getDoubleValues(pState);
        }
        return super.getActionValues(pState);
    }

    private void planning() {
        Set<State> states = q.getAllValidStates();

        for (int i = 0; i < n; i++) {
            State nextState = CollectionsUtils.getRandom(states);
            Action nextAction = getRandomAction(nextState);
            QTableItem item = q.get(nextState, nextAction);
            State finalState = item.getFinalState();
            double reward = item.getReward();
            super.updateQ(nextState, nextAction, reward, finalState);
        }
    }

    private Action getRandomAction(State nextState) {
        Set<Action> actions = q.getAllValidActions(nextState);
        return CollectionsUtils.getRandom(actions);
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nN: ").append(n);
        sb.append("\nEpsilon: ").append(EPSILON);

        return sb.toString();
    }
}
