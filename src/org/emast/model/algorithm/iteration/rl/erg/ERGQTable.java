package org.emast.model.algorithm.iteration.rl.erg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class ERGQTable {

    private List<State> states;
    private List<Action> actions;
    private Double[][] values;
    private Double[][] rewards;
    private Integer[][] freq;
    private State[][] finalStates;

    public ERGQTable(ERGQTable q) {
        this(q.getStates(), q.getActions());

        for (int i = 0; i < states.size(); i++) {
            System.arraycopy(q.getValues()[i], 0, values[i], 0, actions.size());
        }
    }

    public ERGQTable(List<State> pStates, List<Action> pActions) {
        states = pStates;
        actions = pActions;
        values = new Double[states.size()][actions.size()];
        freq = new Integer[states.size()][actions.size()];
        rewards = new Double[states.size()][actions.size()];
        finalStates = new State[states.size()][actions.size()];

        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < actions.size(); j++) {
                values[i][j] = 0d;
                freq[i][j] = 0;
                rewards[i][j] = 0d;
            }
        }
    }

    public ERGQTable(Collection<State> states, Collection<Action> actions) {
        this(new ArrayList<State>(states), new ArrayList<Action>(actions));
    }

    public Double getQValue(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return si >= 0 && ai >= 0 ? values[si][ai] : 0d;
    }
    //TODO: change to set of final states?
    public State getFinalState(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return si >= 0 && ai >= 0 ? finalStates[si][ai] : null;
    }

    public Integer getFrequency(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return si >= 0 && ai >= 0 ? freq[si][ai] : 0;
    }

    public Double getReward(State state, Action action) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);

        return si >= 0 && ai >= 0 ? rewards[si][ai] : 0d;
    }

    public void put(State state, Action action, Double value, Double reward, State finalState) {
        int si = states.indexOf(state);
        int ai = actions.indexOf(action);
        values[si][ai] = value;
        rewards[si][ai] = reward;
        finalStates[si][ai] = finalState;
        freq[si][ai]++;
    }

    public Double[][] getValues() {
        return values;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<State> getStates() {
        return states;
    }

    public String[][] toTable() {
        String[][] table = new String[states.size() + 1][actions.size() + 1];
        table[0][0] = getTitle();
        int i = 1;
        for (State state : states) {
            table[i++][0] = state.getName();
        }
        int j = 1;
        for (Action action : actions) {
            table[0][j++] = action.getName();
        }

        i = 1;
        for (State state : states) {
            j = 1;
            for (Action action : actions) {
                Double value = getQValue(state, action);
                table[i][j] = String.format("%.4g", value);
                j++;
            }
            i++;
        }

        return table;
    }

    protected String getTitle() {
        return "";
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable());
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new HashMap<State, Double>();

        for (State state : states) {
            double max = -Double.MAX_VALUE;
            for (Action action : actions) {
                double value = getQValue(state, action);
                if (value != 0 && value >= max) {
                    max = value;
                }
            }
            map.put(state, max == -Double.MAX_VALUE ? 0 : max);
        }

        return map;
    }

    public Policy getPolicy(boolean pAddZeros) {
        final Policy policy = new Policy();

        for (State state : states) {
            for (Action action : actions) {
                double value = getQValue(state, action);
                if (pAddZeros || value != 0) {
                    policy.put(state, action, value);
                }
            }
        }

        return policy;
    }

    public double getTotalFrequency(State pState) {
        int count = 0;
        int si = states.indexOf(pState);
        for (int i = 0; i < freq[si].length; i++) {
            count += freq[si][i];
        }
        return count;
    }
}
