package org.emast.model.algorithm.reinforcement;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;

public abstract class IterationAlgorithm<M extends MDP> implements PolicyGenerator<M> {

    protected List<Map<State, Double>> values;
    protected int iterations;
    protected double gama = 0.9d;
    protected M model;
    //private static int MAX_IT = 10;

    public IterationAlgorithm() {
        values = new ArrayList<Map<State, Double>>();
        iterations = -1;
    }

    protected Action getBestAction(State state) {
        Action action = null;
        //if has somewhere to go to
        Map<State, Double> currValues = iterations == 0
                ? null
                : values.get(iterations - 1);
        Map<Double, Action> q = getQ(model, currValues, state);
        //if found some action and value
        if (!q.isEmpty()) {
            // get the max value for q
            Double max = Collections.max(q.keySet());
            action = q.get(max);
            // save the max value and position in the policy
            values.get(iterations).put(state, max);
        }
        return action;
    }

    protected Map<Double, Action> getQ(MDP pModel,
            Map<State, Double> pValues, State pState) {
        Map<Double, Action> q = new HashMap<Double, Action>();
        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q values for each state
        for (Action action : actions) {
            double reward = pModel.getRewardFunction().getValue(pState, action);
            double value = reward + getGama() * getSum(pModel, pValues, pState, action);
            q.put(value, action);
        }
        return q;
    }

    protected double getSum(MDP pModel, Map<State, Double> pValues,
            State pState, Action pAction) {
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

    protected double getMaxError(List<Map<State, Double>> pValues, int pN) {
        double maxDif = -Double.MAX_VALUE;

        if (pN == 0) {
            maxDif = Double.MAX_VALUE;
        } else {
            Map<State, Double> map1 = pValues.get(pN - 1);
            Map<State, Double> map2 = pValues.get(pN);

            for (State state : map1.keySet()) {
                Double val1 = map1.get(state);
                Double val2 = map2.get(state);

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

        StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);
        sb.append("\nLast values:\n").append(lvs);


        return sb.toString();
    }
}
