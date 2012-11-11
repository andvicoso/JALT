package org.emast.model.algorithm.iteration.rl;

import java.util.Collection;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;

/**
 *
 * @author Anderson
 */
public class QLearning<M extends MDP> extends IterationAlgorithm<M> {

    private QTable q;
    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private double alpha = 0.5;

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        model = pProblem.getModel();
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
            //environment iteration loop
            do {
                //get random action
                Action action = tf.getAction(model.getActions(), state);
                //get reward
                double reward = model.getRewardFunction().getValue(state, action);
                //go to next state
                State nextState = tf.getBestReachableState(model.getStates(), state, action);
                //get current q value
                double cq = q.get(state, action);
                //get new q value
                double value = reward + (getGama() * getMax(model, nextState)) - cq;
                double newq = cq + alpha * value;
                //save q
                q.put(state, action, newq);
                state = nextState;
                //while there is a valid state to go to
            } while (state != null && !pProblem.getFinalStates().contains(state));

            System.out.println(printResults());
            System.out.println(new GridPrinter().toTable(q.getStateValue(), 3, 3));
            System.out.println(pProblem.toString(q.getPolicy()));
            //while  did not reach the max iteration
        } while (getError(lastq) > pProblem.getError());//iterations < MAX_ITERATIONS

        return q.getPolicy();
    }

    protected double getMax(MDP pModel, State pState) {
        Double max = null;

        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = q.get(pState, action);
            if (max == null || value > max) {
                max = value;
            }
        }

        return max;
    }

    @Override
    public String printResults() {
        String lvs = q.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);
        sb.append("\nLast values:\n").append(lvs);


        return sb.toString();
    }

    private double getError(QTable lastq) {
        double maxDif = -Double.MAX_VALUE;

        Map<State, Double> lastv = lastq.getStateValue();
        Map<State, Double> v = q.getStateValue();

        if (iterations == 0) {
            maxDif = Double.MAX_VALUE;
        } else {
            for (State state : lastv.keySet()) {
                Double val1 = lastv.get(state);
                Double val2 = v.get(state);

                if (val1 == null || val2 == null) {
                    break;
                }

                double dif = Math.abs(val2 - val1);
                if (dif > maxDif) {
                    maxDif = dif;
                }
            }
        }

        return maxDif;
    }
}
