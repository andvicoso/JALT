package org.emast.model.algorithm.iteration.rl;

import java.util.Collection;
import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.actionchooser.ActionChooser;
import org.emast.model.algorithm.actionchooser.RandomActionChooser;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public abstract class AbstractQLearning<M extends MDP>
        extends IterationAlgorithm<M, Policy>
        implements PolicyGenerator<M> {

    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private final double alpha = ALPHA;
    private int steps;
    protected QTable<? extends QTableItem> q;
    private ActionChooser actionChooser;

    public AbstractQLearning(QTable q) {
        this.q = q;
    }

    public AbstractQLearning() {
    }

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        init(pProblem, pParameters);
        return doRun(pProblem, pParameters);
    }

    protected void init(Problem<M> pProblem, Object... pParameters) {
        model = pProblem.getModel();
        if (actionChooser == null) {
            actionChooser = new RandomActionChooser();
        }
        if (q == null) {
            q = model instanceof ERG
                    ? new ERGQTable(model.getStates(), model.getActions())
                    : new QTable<QTableItem>(model.getStates(), model.getActions());
        }
    }

    protected Policy doRun(Problem<M> pProblem, Object... pParameters) {
        QTable lastq;
        //start the main loop
        do {
            steps = 0;
            lastq = q.clone();
            //get initial state
            State state = pProblem.getInitialStates().get(0);
            Action action;
            //environment iteration loop
            do {
                //get action for state
                action = actionChooser.choose(getActionValues(state), state);
                if (action != null) {
                    //get reward for current action and state
                    double reward = model.getRewardFunction().getValue(state, action);
                    //get next state
                    State nextState = model.getTransitionFunction().getBestReachableState(model.getStates(), state, action);
                    //if nextState eq null, stay in the same state and try a different action
                    if (nextState != null) {
                        updateQ(state, action, reward, nextState);
                        //go to next state
                        state = nextState;
                    }
                }
                steps++;
                //while there is a valid state to go to
            } while (!isStopSteps(action, state, pProblem));

            episodes++;
            Log.info("episodes: " + episodes + ". steps: " + steps);
        } while (!isStopEpisodes(lastq));

        return q.getPolicy(false);
    }

    protected double getMax(State pState) {
        Double max = null;

        Collection<Action> actions = model.getTransitionFunction().getActionsFrom(model.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = q.getValue(pState, action);
            if (max == null || value > max) {
                max = value;
            }
        }

        if (max == null) {
            max = 0d;
        }

        return max;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder(super.printResults());
        //sb.append("\nLast values:\n").append(q.toString());
        return sb.toString();
    }

    public void setActionChooser(ActionChooser actionChooser) {
        this.actionChooser = actionChooser;
    }

    public ActionChooser getActionChooser() {
        return actionChooser;
    }

    public QTable getQTable() {
        return q;
    }

    public double getAlpha() {
        return alpha;
    }

    protected boolean isStopSteps(Action action, State state, Problem<M> pProblem) {
        return action == null || state == null || pProblem.getFinalStates().contains(state);
    }

    protected boolean isStopEpisodes(QTable lastq) {
        // return episodes < MAX_ITERATIONS;
        return getError(lastq.getStateValue(), q.getStateValue()) < ERROR;
    }

    protected Map<Action, Double> getActionValues(State pState) {
        return model.getTransitionFunction().getActionValues(model.getActions(), pState);
    }

    protected abstract double computeQ(State state, Action action, double reward, State nextState);

    protected void updateQ(State state, Action action, double reward, State nextState) {
        double qValue = computeQ(state, action, reward, nextState);
        q.updateQ(model, qValue, state, action, reward, nextState);
    }
}
