package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class ERGQLearning extends IterationAlgorithm<ERG> {

    private static final int MAX_IT = 10;
    private static final double BAD_VALUE = -20d;
    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private double alpha = 0.5;
    private ERGQTable q;
    // private Policy policy;
    private Map<Expression, Double> expSum;
    private Map<Expression, Integer> expCount;
    private Expression badExpression;

    public ERGQLearning() {
        expCount = new HashMap<Expression, Integer>();
        expSum = new HashMap<Expression, Double>();
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
            badExpression = getBadExpression();

//            if (badProp != null) {
//                break;
//            }
             System.out.print(iterations);
            //while did not reach the max iteration
        } while (iterations < 1);//getError(lastq.getStateValue(), q.getStateValue()) > pProblem.getError());//

        return new Policy();//q.getPolicy(false);//TODO:
    }

    protected void updateQTable(State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = q.getQValue(state, action);
        //get new q value
        double value = reward + (getGama() * getMax(model, nextState)) - cq;
        double newq = cq + alpha * value;
        //save q
        q.put(state, action, newq, reward, nextState);
        //save q for the next state expression
        updateProps(value, nextState);
    }

    protected void updateProps(double value, State nextState) {
        Expression exp = model.getPropositionFunction().getExpressionForState(nextState);
        if (exp != null) {
            double sum = 0;
            int count = 0;

            if (expSum.containsKey(exp)) {
                sum = expSum.get(exp);
            }
            if (expCount.containsKey(exp)) {
                count = expCount.get(exp);
            }

            expSum.put(exp, sum + value);
            expCount.put(exp, count + 1);
        }
    }

    private double getMax(MDP pModel, State pState) {
        Double max = null;

        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = q.getQValue(pState, action);
            if (max == null || value > max) {
                max = value;
            }
        }

        if (max == null) {
            max = 0d;
        }

        return max;
    }

    public Map<Expression, Double> getExpsValues() {
        Map<Expression, Double> values = new HashMap<Expression, Double>();

        for (Expression p : expSum.keySet()) {
            double value = 0;
            Double sum = expSum.get(p);
            Integer count = expCount.get(p);
            if (sum != null && count != null) {
                value = sum / count;
            }
            values.put(p, value);
        }

        return values;
    }

    private Expression getBadExpression() {
        Map<Expression, Double> values = getExpsValues();

        for (Map.Entry<Expression, Double> entry : values.entrySet()) {
            Expression proposition = entry.getKey();
            Double value = entry.getValue();
            if (value < BAD_VALUE) {
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

    public Expression getBadProp() {
        return badExpression;
    }
}
