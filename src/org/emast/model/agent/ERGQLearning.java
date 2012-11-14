package org.emast.model.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class ERGQLearning extends QLearning<ERG> {

    private Policy policy;
    private Map<Proposition, Double> propSum;
    private Map<Proposition, Integer> propCount;

    public ERGQLearning() {
        propCount = new HashMap<Proposition, Integer>();
        propSum = new HashMap<Proposition, Double>();
    }

    @Override
    protected Action getAction(State state) {
        Map<Action, Double> values = policy.get(state);
        return values == null ? null : CollectionsUtils.draw(values);
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        policy = (Policy) pParameters[0];
        return super.run(pProblem, pParameters);
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

    @Override
    protected void updateQTable(State state, Action action, double reward, State nextState) {
        super.updateQTable(state, action, reward, nextState);

        Set<Proposition> props = model.getPropositionFunction().getPropositionsForState(nextState);
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

    @Override
    public String printResults() {
        return super.printResults();
    }
    
    
}
