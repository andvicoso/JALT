package org.emast.model.algorithm.reachability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.action.Action;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.model.MDP;
import org.emast.model.model.SRG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

public class ValueIterationSRGAlgorithm<M extends MDP & SRG>
        extends ValueIterationAlgorithm<M> {

    @Override
    public Policy run(Problem<M> pProblem) {
        M model = pProblem.getModel();
        // Initialize the variables
        final List<Map<State, Double>> values = new ArrayList<Map<State, Double>>();
        final List<Policy> pis = new ArrayList<Policy>();
        int n = 0;
        // Initilize the policies with random values
        for (final State state : model.getStates()) {
            try {
                values.get(0).put(state, model.getPropositionFunction().satisfies(model.getPropositions(), state,
                        model.getGoal()) ? 1d : 0d);
            } catch (EvaluationException ex) {
            }
        }
        // Start the main loop
        // When the maximmum error is greater than the defined error,
        // the best policy is found
        do {
            for (final State state : model.getStates()) {
                final Map<Double, Action> q = getQ(model, n == 0
                        ? null : values.get(n - 1), state);
                // get the max value for q
                final Double max = Collections.max(q.keySet());
                final Action actions = q.get(max);
                // save the max value and position in the policy
                values.get(n).put(state, max);
                pis.get(n).put(state, actions);
            }
            n++;
        } while (getMaxError(values, n) > (1.0 - pProblem.getError()));

        return pis.get(n);
    }
}