package org.emast.model.algorithm.reachability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.SRG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

public class ValueIterationSRGAlgorithm<M extends SRG>
        extends ValueIterationAlgorithm<M> {

    private int iterations = 0;

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        M model = pProblem.getModel();
        // Initialize the variables
        final List<Map<State, Double>> values = new ArrayList<Map<State, Double>>();
        final List<Policy> pis = new ArrayList<Policy>();
        // Initilize the policies with random values
        for (final State state : model.getStates()) {
            try {
                double value = model.getPropositionFunction().satisfies(state, model.getGoal()) ? 1d : 0d;
                values.get(0).put(state, value);
            } catch (InvalidExpressionException ex) {
            }
        }
        // Start the main loop
        // When the maximmum error is greater than the defined error,
        // the best policy is found
        do {
            pis.add(new Policy());

            for (final State state : model.getStates()) {
                final Map<Double, Action> q = getQ(model, iterations == 0
                        ? null : values.get(iterations - 1), state);
                // get the max value for q
                final Double max = Collections.max(q.keySet());
                final Action actions = q.get(max);
                // save the max value and position in the policy
                values.get(iterations).put(state, max);
                pis.get(iterations).put(state, actions);
            }
            iterations++;
        } while (getMaxError(values, iterations) > (1.0 - pProblem.getError()));

        return pis.get(iterations);
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);

        return sb.toString();
    }
}