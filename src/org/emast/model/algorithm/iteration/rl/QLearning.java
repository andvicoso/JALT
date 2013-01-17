package org.emast.model.algorithm.iteration.rl;

import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class QLearning<M extends MDP> extends IterationAlgorithm<M> {

    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private double alpha = 0.5;
    private QTable q;
    private FrequencyTable frequency;
    private NTable rewardTable;

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        model = pProblem.getModel();
        //TODO: here or inside the main loop?
        frequency = new FrequencyTable(model.getStates(), model.getActions());
        rewardTable = new NTable(model.getStates(), model.getActions());
        //set initial q
        q = new QTable(model.getStates(), model.getActions());
        TransitionFunction tf = model.getTransitionFunction();
        QTable lastq;
        //start the main loop
        do {
            iterations++;
            lastq = new QTable(q);
            //get initial state
            State state = pProblem.getInitialStates().get(0);
            Action action;
            //environment iteration loop
            do {
                //get random action
                action = getAction(state);
                if (action != null) {
                    //get reward
                    double reward = model.getRewardFunction().getValue(state, action);
                    //get next state
                    State nextState = tf.getBestReachableState(model.getStates(), state, action);

                    if (nextState != null) {
                        updateQTable(state, action, reward, nextState);
                    }
                    rewardTable.put(state, action, reward);
                    frequency.inc(state, action);
                    //go to next state
                    state = nextState;
                }
                //while there is a valid state to go to
            } while (action != null && state != null && !pProblem.getFinalStates().contains(state));
//            System.out.println(printResults());
//            System.out.println(new GridPrinter().toTable(q.getStateValue(), 5, 5));
//            System.out.println(pProblem.toString(q.getPolicy()));
            //while  did not reach the max iteration
        } while (getError(lastq.getStateValue(), q.getStateValue()) > pProblem.getError());//iterations < 100);//

        return q.getPolicy(false);
    }

    protected void updateQTable(State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = q.get(state, action);
        //get new q value
        double value = reward + (getGama() * getMax(model, nextState)) - cq;
        double newq = cq + alpha * value;
        //save q
        q.put(state, action, newq);
    }

    private double getMax(MDP pModel, State pState) {
        Double max = null;

        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = q.get(pState, action);
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
        sb.append("\nLast values:\n").append(q.toString());

        return sb.toString();
    }

    protected Action getAction(State state) {
        return model.getTransitionFunction().getAction(model.getActions(), state);
    }

    public QTable getQTable() {
        return q;
    }

    public double getAlpha() {
        return alpha;
    }

    public FrequencyTable getFrequencyTable() {
        return frequency;
    }

    public NTable getRewardTable() {
        return rewardTable;
    }
}
