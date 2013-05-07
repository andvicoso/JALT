package org.emast.model.algorithm.iteration.rl.erg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class ERGQLearningIndivProp extends QLearning<ERG> {

    // private Policy policy;
    private Map<Proposition, Double> propSum;
    private Map<Proposition, Integer> propCount;
    private Proposition badProp;

    public ERGQLearningIndivProp() {
        super(new ERGQTable(null));//TODO
        propCount = new HashMap<Proposition, Integer>();
        propSum = new HashMap<Proposition, Double>();
    }

    protected void updateProps(double reward, State nextState) {
        Set<Proposition> props = model.getPropositionFunction().getPropositionsForState(nextState);
        if (props != null) {
            double value = reward / props.size();
            double sum = 0;
            int count = 0;

            for (Proposition p : props) {
                if (propSum.containsKey(p)) {
                    sum = propSum.get(p);
                }
                if (propCount.containsKey(p)) {
                    count = propCount.get(p);
                }

                propSum.put(p, sum + value);
                propCount.put(p, count + 1);
            }
        }
    }

    public Map<Proposition, Double> getPropsValues() {
        Map<Proposition, Double> values = new HashMap<Proposition, Double>();

        for (Proposition p : propSum.keySet()) {
            double value = 0;
            Double sum = propSum.get(p);
            Integer count = propCount.get(p);
            if (sum != null && count != null) {
                value = sum / count;
            }
            values.put(p, value);
        }

        return values;
    }

    private Proposition getPropositionAbove(double pThreshold) {
        Map<Proposition, Double> values = getPropsValues();

        for (Map.Entry<Proposition, Double> entry : values.entrySet()) {
            Proposition proposition = entry.getKey();
            Double value = entry.getValue();
            if (value <= pThreshold) {
                return proposition;
            }
        }

        return null;
    }

    public Proposition getBadProp() {
        return badProp;
    }
}
