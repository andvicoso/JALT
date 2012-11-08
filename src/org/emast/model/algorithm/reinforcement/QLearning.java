package org.emast.model.algorithm.reinforcement;

import java.util.HashMap;
import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class QLearning<M extends MDP> extends IterationAlgorithm<M> {

    public static final int MAX_ITERATIONS = 1000;

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        //create the policy
        Policy pi = new Policy();
        model = pProblem.getModel();
        // Start the main loop
        // When the maximmum error is greater than the defined error,
        // the best policy is found
        do {
            iterations++;
            //set initial values
            values.add(iterations, new HashMap<State, Double>());

            State state = ProblemFactory.getRandomState(model);
            //main loop
            do {
                //get the valid action associated with the state
                Action action = CollectionsUtils.getRandom(model.getActions());
                state = model.getTransitionFunction().getBestReachableState(model.getStates(), state, action);
                if (state != null) {
                    Action act = getBestAction(state);
                    //add to the policy
                    pi.put(state, act);
                }
                //while there is a valid action to execute
            } while (state != null);
            //System.out.println(printResults());
            System.out.println(iterations);
            //while  did not reach the max iteration
        } while (iterations < MAX_ITERATIONS);

        return pi;
    }
}
