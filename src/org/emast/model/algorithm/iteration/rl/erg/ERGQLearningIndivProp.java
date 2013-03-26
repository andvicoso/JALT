package org.emast.model.algorithm.iteration.rl.erg;

import org.emast.model.algorithm.table.erg.ERGQTable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author anderson
 */
public class ERGQLearningIndivProp extends IterationAlgorithm<ERG> {

    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private double alpha = 0.5;
    private ERGQTable q;
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
        TransitionFunction tf = model.getTransitionFunction();
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
                    State nextState = tf.getBestReachableState(model.getStates(), state, action);

                    if (nextState != null) {
                        updateQTable(state, action, reward, nextState);
                    }
                    //go to next state
                    state = nextState;
                }
                //while there is a valid state to go to
            } while (action != null && state != null && !pProblem.getFinalStates().contains(state));
//            System.out.println(printResults());
//            System.out.println(new GridPrinter().toTable(q.getStateValue(), 5, 5));
//            System.out.println(pProblem.toString(q.getPolicy()));
            badProp = getBadProposition();

//            if (badProp != null) {
//                break;
//            }
            //while did not reach the max iteration
        } while (iterations < MAX_ITERATIONS);//getError(lastq.getStateValue(), q.getStateValue()) > pProblem.getError());//

        return new Policy();//q.getPolicy(false);//TODO:
    }

    protected void updateQTable(State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = q.get(state, action).getValue();
        //get new q value
        double value = reward + (getGama() * getMax(model, nextState)) - cq;
        double newq = cq + alpha * value;
        //save q
        //q.put(state, action, newq, reward, nextState);
        Expression exp = model.getPropositionFunction().getExpressionForState(nextState);
        //save q
        q.put(state, action, new ERGQTableItem(newq, reward, getFrequency(state, action), nextState, exp));
        updateProps(reward, nextState);
    }

    private Integer getFrequency(State state, Action action) {
        ERGQTableItem item = q.get(state, action);
        return item != null ? item.getFrequency() + 1 : 1;
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

    private double getMax(MDP pModel, State pState) {
        Double max = null;

        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = q.get(pState, action).getValue();
            if (max == null || value > max) {
                max = value;
            }
        }

        if (max == null) {
            max = 0d;
        }

        return max;
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

    private Proposition getBadProposition() {
        Map<Proposition, Double> values = getPropsValues();

        for (Map.Entry<Proposition, Double> entry : values.entrySet()) {
            Proposition proposition = entry.getKey();
            Double value = entry.getValue();
            if (value < BAD_EXP_VALUE) {
                return proposition;
            }
        }

        return null;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nLast values:\n").append(q.toString());

        return sb.toString();
    }

    protected Action getAction(State state) {
        return model.getTransitionFunction().getAction(model.getActions(), state);
    }

    public ERGQTable getQTable() {
        return q;
    }

    public double getAlpha() {
        return alpha;
    }

    public Proposition getBadProp() {
        return badProp;
    }
}
