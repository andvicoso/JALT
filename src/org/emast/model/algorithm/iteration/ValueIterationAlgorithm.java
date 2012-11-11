package org.emast.model.algorithm.iteration;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.GridPrinter;

public class ValueIterationAlgorithm<M extends MDP> extends IterationAlgorithm<M> {

    private Map<State, Double> lastv;
    private Map<State, Double> v;

    public ValueIterationAlgorithm() {
        lastv = Collections.EMPTY_MAP;
        v = Collections.EMPTY_MAP;
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
            //set initial v
            v = new HashMap<State, Double>();
            //create the policy
            pi = new Policy();
            //for each state
            for (State state : model.getStates()) {
                Map<Double, Action> q = getQ(model, state);
                //if found some action and value
                if (!q.isEmpty()) {
                    // get the max value for q
                    Double max = Collections.max(q.keySet());
                    Action action = q.get(max);
                    // save the max value and position in the policy
                    v.put(state, max);
                    //add to the policy
                    pi.put(state, action);
                }
            }
            System.out.println(printResults());
            lastv = v;
        } while (getError() > pProblem.getError());

        return pi;
    }

    private Map<Double, Action> getQ(MDP pModel, State pState) {
        Map<Double, Action> q = new HashMap<Double, Action>();
        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            double reward = pModel.getRewardFunction().getValue(pState, action);
            double value = reward + getGama() * getSum(pModel, pState, action);
            q.put(value, action);
        }
        return q;
    }

    private double getSum(MDP pModel, State pState, Action pAction) {
        double sum = 0;

        if (!lastv.isEmpty()) {
            Map<State, Double> rv = pModel.getTransitionFunction().getReachableStatesValues(
                    pModel.getStates(), pState, pAction);

            for (Map.Entry<State, Double> entry : rv.entrySet()) {
                State state = entry.getKey();
                Double value = entry.getValue();
                if (lastv.containsKey(state)) {
                    sum += value * lastv.get(state);
                }
            }
        }

        return sum;
    }

    @Override
    public String printResults() {
        String lvs;

        if (model instanceof Grid) {
            int rows = ((Grid) model).getRows();
            int cols = ((Grid) model).getCols();
            lvs = new GridPrinter().toTable(lastv, rows, cols);
        } else {
            lvs = lastv.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);
        sb.append("\nLast values:\n").append(lvs);


        return sb.toString();
    }

    private double getError() {
        double maxDif = -Double.MAX_VALUE;

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
