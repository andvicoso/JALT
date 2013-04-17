package org.emast.model.algorithm.iteration.rl.erg;

import org.emast.model.algorithm.table.erg.ERGQTable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class ERGQLearningIndivProp extends ERGQLearning {

    // private Policy policy;
    private Map<Proposition, Double> propSum;
    private Map<Proposition, Integer> propCount;
    private Proposition badProp;

    public ERGQLearningIndivProp() {
        propCount = new HashMap<Proposition, Integer>();
        propSum = new HashMap<Proposition, Double>();
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        model = pProblem.getModel();
        //set initial q
        q = new ERGQTable(model.getStates(), model.getActions());
        ERGQTable lastq;
        //start the main loop
        do {
            iterations++;
            lastq = new ERGQTable(q);
            //get initial state
            State state = pProblem.getInitialStates().get(0);
            Action action;
            //environment iteration loop
            do {
                //get random action
                action = getAction(state);
                if (action != null) {
                    //get reward
                    double reward = model.getRewardFunction().getValue(state, action);
                    //get next state
                    State nextState = getNextState(state, action);

                    if (nextState != null) {
                        updateQTable(state, action, reward, nextState);
                    }
                    //go to next state
                    state = nextState;
                }
                //while there is a valid state to go to
            } while (action != null && state != null && !pProblem.getFinalStates().contains(state));
            //badProp = getBadProposition();

//            if (badProp != null) {
//                break;
//            }
            //while did not reach the max iteration
        } while (isStop(lastq));

        return new Policy();//q.getPolicy(false);//TODO:
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
