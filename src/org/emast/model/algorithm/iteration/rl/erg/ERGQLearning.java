package org.emast.model.algorithm.iteration.rl.erg;

import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import java.util.Collection;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;
/**
 *
 * @author anderson
 */
public class ERGQLearning extends IterationAlgorithm<ERG> {

    /**
     * The learning rate. The learning rate determines to what extent the newly acquired information will
     * override the old information. A factor of 0 will make the agent not learn anything, while a factor of 1
     * would make the agent consider only the most recent information.
     */
    private double alpha = 0.5;
    private ERGQTable q;
    private Expression badExpression;

    public ERGQLearning(ERGQTable q) {
        this.q = q;
    }

    public ERGQLearning() {
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        model = pProblem.getModel();
        if (q == null) {
            q = new ERGQTable(model.getStates(), model.getActions());
        }
        TransitionFunction tf = model.getTransitionFunction();
        ERGQTable lastq;

        //start the main loop
        do {
            lastq = new ERGQTable(q);
            //get initial state
            State state = pProblem.getInitialStates().get(0);
            Action action;
            //environment iteration loop
            do {
                //get random action
                action = model.getTransitionFunction().getAction(model.getActions(), state);
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
            
            iterations++;
//            System.out.println(printResults());
//            System.out.println(new GridPrinter().toTable(q.getStateValue(), 5, 5));
//            System.out.println(pProblem.toString(q.getPolicy()));
            badExpression = q.getBadExpression();

            System.out.print(iterations + " ");
            //System.out.println(new GridPrinter().toTable(q.toTable()));
            //while did not reach the max iteration
        } while (iterations < MAX_ITERATIONS);//getError(lastq.getStateValue(), q.getStateValue()) > ERROR//pProblem.getError()

        return new Policy();//q.getPolicy(false);//TODO:
    }

    protected void updateQTable(State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = q.get(state, action).getValue();
        //get new q value
        double value = reward + (getGama() * getMax(model, nextState)) - cq;
        double newq = cq + alpha * value;
        //get expression for state
        Expression exp = model.getPropositionFunction().getExpressionForState(nextState);
        //save q
        q.put(state, action, new ERGQTableItem(newq, reward, getFrequency(state, action), nextState, exp));
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

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nLast values:\n").append(q.toString());

        return sb.toString();
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

    private Integer getFrequency(State state, Action action) {
        ERGQTableItem item = q.get(state, action);
        return item != null ? item.getFrequency() + 1 : 1;
    }
}
