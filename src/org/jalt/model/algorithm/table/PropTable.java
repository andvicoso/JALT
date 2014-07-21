package org.jalt.model.algorithm.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridPrinter;

/**
 *
 * @author andvicoso
 */
public class PropTable {

    private List<State> states;
    private List<Proposition> props;
    private Double[][] values;

    public PropTable(PropTable q) {
        this(q.getStates(), q.getPropositions());

        for (int i = 0; i < states.size(); i++) {
            System.arraycopy(q.getValues()[i], 0, values[i], 0, props.size());
        }
    }

    public PropTable(List<State> pStates, List<Proposition> pPropositions) {
        states = pStates;
        props = pPropositions;
        values = new Double[states.size()][props.size()];

        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < props.size(); j++) {
                values[i][j] = 0d;
            }
        }
    }

    public PropTable(Collection<State> states, Collection<Proposition> props) {
        this(new ArrayList<State>(states), new ArrayList<Proposition>(props));
    }

    Double[][] getValues() {
        return values;
    }

    List<Proposition> getPropositions() {
        return props;
    }

    List<State> getStates() {
        return states;
    }

    public double get(State state, Proposition prop) {
        int si = states.indexOf(state);
        int ai = props.indexOf(prop);

        return values[si][ai];
    }

    public void put(State state, Proposition prop, Double value) {
        int si = states.indexOf(state);
        int ai = props.indexOf(prop);
        values[si][ai] = value;
    }

    public Map<State, Double> getStateValue() {
        final Map<State, Double> map = new TreeMap<State, Double>();

        for (State state : states) {
            double max = 0;
            Proposition best = null;
            for (Proposition prop : props) {
                double value = get(state, prop);
                if (value >= max) {
                    max = value;
                    best = prop;
                }
            }
            if (best != null) {
                map.put(state, max);
            }
        }

        return map;
    }

    public Map<Proposition, Double> getPropValue() {
        final Map<Proposition, Double> map = new TreeMap<Proposition, Double>();

        for (Proposition prop : props) {
            double sum = 0;
            int count = 0;
            for (State state : states) {
                double value = get(state, prop);

                if (value != 0) {
                    count++;
                    sum += value;
                }
            }
            if (count > 0) {
                map.put(prop, sum / count);
            }
        }

        return map;
    }

    public String[][] toTable() {
        String[][] table = new String[states.size() + 1][props.size() + 1];
        int i = 1;
        for (State state : states) {
            table[i++][0] = state.getName();
        }
        int j = 1;
        for (Proposition prop : props) {
            table[0][j++] = prop.getName();
        }

        i = 1;
        for (State state : states) {
            j = 1;
            for (Proposition prop : props) {
                Double value = get(state, prop);
                table[i][j] = String.format("%.4g", value);
                j++;
            }
            i++;
        }

        return table;
    }

    @Override
    public String toString() {
        return new GridPrinter().toTable(toTable());
    }
}
