package org.emast.model.algorithm.reinforcement;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

public class ValueIterationAlgorithm<M extends MDP> extends IterationAlgorithm<M> {

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        Policy pi;
        model = pProblem.getModel();
        // Start the main loop
        // When the maximmum error is greater than the defined error,
        // the best policy is found
        do {
            iterations++;
            //set initial values
            values.add(iterations, new HashMap<State, Double>());
            //create the policy
            pi = new Policy();
            //for each state
            for (State state : model.getStates()) {
                Action action = getBestAction(state);
                //add to the policy
                pi.put(state, action);
            }
            System.out.println(printResults());
        } while (getError() > pProblem.getError());

        return pi;
    }
}
