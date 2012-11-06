package org.emast.model.algorithm.reinforcement;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;

public class ValueIterationAlgorithm<M extends MDP> implements PolicyGenerator<M> {

    private List<Map<State, Double>> values;
    private int iterations;
    private double gama = 0.9d;
    private M model;
    private static final int MAX_IT = 10;

    public ValueIterationAlgorithm() {
        values = new ArrayList<Map<State, Double>>();
        iterations = -1;
    }

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
            for (final State state : model.getStates()) {
                final Map<State, Double> currValues = iterations == 0
                        ? null
                        : values.get(iterations - 1);
                final Map<Double, Action> q = getQ(model, currValues, state);
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
            System.out.println(printResults());
        } while (iterations < MAX_IT);//getError() > pProblem.getError());

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
            Map<State, Double> rv = pModel.getTransitionFunction().getReachableStatesValues(
                    pModel.getStates(), pState, pAction);

            for (Map.Entry<State, Double> entry : rv.entrySet()) {
                State state = entry.getKey();
                Double value = entry.getValue();
                if (pValues.containsKey(state)) {
                    sum += value * pValues.get(state);
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

    @Override
    public String printResults() {
        String lvs;
        Map<State, Double> lastValues = values.get(iterations);
        if (model instanceof Grid) {
            int rows = ((Grid) model).getRows();
            int cols = ((Grid) model).getCols();
            lvs = new GridPrinter().toTable(lastValues, rows, cols);
        } else {
            lvs = lastValues.toString();
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);
        sb.append("\nLast values:\n").append(lvs);


        return sb.toString();
    }
}
