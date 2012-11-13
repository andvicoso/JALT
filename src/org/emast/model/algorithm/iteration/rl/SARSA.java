package org.emast.model.algorithm.iteration.rl;

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
public class SARSA<M extends MDP> extends IterationAlgorithm<M> {

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
            Action action;
            //environment iteration loop
            do {
                //get random action
                action = tf.getAction(model.getActions(), state);
                if (action != null) {
                    //get reward
                    double reward = model.getRewardFunction().getValue(state, action);
                    //go to next state
                    State nextState = tf.getBestReachableState(model.getStates(), state, action);
                    if (nextState != null) {
                        //get next action
                        Action nextAction = tf.getAction(model.getActions(), nextState);
                        //get current q value
                        double cq = q.get(state, action);
                        //get new q value
                        double value = reward + (getGama() * q.get(nextState, nextAction)) - cq;
                        double newq = cq + alpha * value;
                        //save q
                        q.put(state, action, newq);
                    }
                    state = nextState;
                }
                //while there is a valid state to go to
            } while (action != null && state != null && !pProblem.getFinalStates().contains(state));
//            System.out.println(printResults());
//            System.out.println(new GridPrinter().toTable(q.getStateValue(), 5, 5));
//            System.out.println(pProblem.toString(q.getPolicy()));
            //while  did not reach the max iteration
        } while (iterations < MAX_ITERATIONS);//getError(lastq.getStateValue(), q.getStateValue()) > pProblem.getError()

        return q.getPolicy();
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nLast values:\n").append(q.toString());


        return sb.toString();
    }
}
