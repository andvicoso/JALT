package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Set;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.action.Action;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class ERGQLearningStopBadExpression extends ERGQLearning {

    protected final double threshold;
    protected final Set<Expression> avoid;

    public ERGQLearningStopBadExpression(ERGQTable q, double threshold, Set<Expression> avoid) {
        super(q);
        this.q = q;
        this.threshold = threshold;
        this.avoid = avoid;
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        model = pProblem.getModel();
        ERGQTable lastq;

        if (q == null) {
            q = new ERGQTable(model.getStates(), model.getActions());
        }

        //start the main loop
        do {
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
                //break if found a bad expression
                if (getBadExpression() != null) {
                    break;
                }
                //while there is a valid state to go to
            } while (action != null && state != null && !pProblem.getFinalStates().contains(state));

            iterations++;

            System.out.print(iterations + " ");
        } while (isStop(lastq));

        return new Policy();//q.getPolicy(false);//TODO:
    }

    public Expression getBadExpression() {
        if (q != null) {
            ThresholdChooser<Expression> badChooser =
                    new ThresholdChooser<Expression>(threshold, true);

            Set<Expression> badExps = badChooser.choose(q.getExpsValues());

            badExps.removeAll(avoid);

            if (!badExps.isEmpty()) {
                return badExps.iterator().next();
            }
        }
        return null;
    }
}
