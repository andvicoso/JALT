package org.emast.model.algorithm.reinforcement;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

public class ValueIterationAlgorithm<M extends MDP> implements Algorithm<M, Policy> {

    private List<Map<State, Double>> values;
    private int iterations;
    private double gama = 0.9d;

    public ValueIterationAlgorithm() {
        values = new ArrayList<Map<State, Double>>();
        iterations = -1;
    }

    @Override
    public Policy run(final Problem<M> pProblem) {
        Policy pi;
        M model = pProblem.getModel();
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
            for (final State state : model.getStates()) {
                final Map<State, Double> pValues = iterations == 0
                        ? null
                        : values.get(iterations - 1);
                final Map<Double, Action> q = getQ(model, pValues, state);
                //if found some action and value
                if (!q.isEmpty()) {
                    // get the max value for q
                    final Double max = Collections.max(q.keySet());
                    final Action action = q.get(max);
                    // save the max value and position in the policy
                    values.get(iterations).put(state, max);
                    //add to the policy
                    pi.put(state, action);
                }
            }
        } while (getError() > pProblem.getError());

        return pi;
    }

    protected Map<Double, Action> getQ(final MDP pModel,
            final Map<State, Double> pValues, final State pState) {
        final Map<Double, Action> q = new HashMap<Double, Action>();
        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q values for each state
        for (final Action action : actions) {
            final double reward = pModel.getRewardFunction().getValue(pState, action);
            final double value = reward + getGama() * getSum(pModel, pValues, pState, action);
            q.put(value, action);
        }
        return q;
    }

    protected double getSum(final MDP pModel, final Map<State, Double> pValues,
            final State pState, final Action pAction) {
        double sum = 0;

        if (pValues != null) {
            for (final State stateLine : pModel.getStates()) {
                final double trans = pModel.getTransitionFunction().getValue(
                        pState, stateLine, pAction);
                // get the q value based on the last value (n - 1)
                // if exists
                if (pValues.get(stateLine) != null) {
                    sum += trans * pValues.get(stateLine);
                }
            }
        }

        return sum;
    }

    protected double getMaxError(final List<Map<State, Double>> pValues,
            final int pN) {
        double maxDif = -Double.MAX_VALUE;

        if (pN == 0) {
            maxDif = Double.MAX_VALUE;
        } else {
            final Map<State, Double> map1 = pValues.get(pN - 1);
            final Map<State, Double> map2 = pValues.get(pN);

            for (final State state : map1.keySet()) {
                final Double val1 = map1.get(state);
                final Double val2 = map2.get(state);

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

    public List<Map<State, Double>> getValues() {
        return values;
    }

    public int getIterations() {
        return iterations;
    }

    public double getError() {
        return getMaxError(values, iterations);
    }

    public double getGama() {
        return gama;
    }
}
