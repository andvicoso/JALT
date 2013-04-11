package org.emast.model.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.ValueIteration;
import static org.emast.model.algorithm.iteration.rl.erg.ERGFactory.getBadExpressions;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.chooser.MinValueChooser;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.Utils;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class QLearningERGController extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 3;
    private long init;
    private long end;
    private ERGQTable q;
    private ERGQLearning learning;
    private Set<Expression> avoid;
    private Set<Expression> attract;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        avoid = new HashSet<Expression>();
        attract = new HashSet<Expression>();

        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();
        Expression newPreservGoal;

        // findBestPlan(pProblem);

        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            //1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p);

            //2. GET NEW PRESERVATION GOAL FROM QLEARNING ITERATIONS
            newPreservGoal = getWorstExpression(learning.getQTable().getExpsValues(), avoid);

            //3. REDUCE Q FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE NEW PRESERVATION GOAL
            if (newPreservGoal != null) {
                model.getPreservationGoal().add(newPreservGoal, BinaryOperator.AND);

                avoid.add(newPreservGoal);
                System.out.println("Avoid: " + avoid);

                changeQ(model);
                System.out.println("QTable: \n" + q.toString());
            }
        } while (newPreservGoal != null && (iterations < MAX_IT));

        return new Policy();//learning.getQTable().getPolicy(false);//TODO
    }

    @Override
    public String printResults() {
        return learning.printResults();
    }

    private void findBestPlan(Problem<ERG> pProblem) {
        Log.info("\nValue Iteration");
        //ERG model = pProblem.getModel();
        ValueIteration vi = new ValueIteration();
        initTime();
        Policy pi = vi.run(pProblem, (Object) null);
        endTime();
//        State st = pProblem.getInitialStates().get(0);
//        double sum = 0;
//        do {
//            Action a = pi.getBestAction(st);
//            sum += model.getRewardFunction().getValue(st, a);
//            st = model.getTransitionFunction().getBestReachableState(model.getStates(), st, a);
//        } while (st != null && !pProblem.getFinalStates().contains(st));

        Log.info("\nIterations: " + vi.getIterations());
        //Log.info("Best plan reward value: " + sum);
        Log.info("Best policy: " + pProblem.toString(pi));
    }

    private void initTime() {
        init = System.currentTimeMillis();
    }

    private void endTime() {
        end = System.currentTimeMillis();
        long diff = end - init;
        Log.info("\nTime: " + Utils.toTimeString(diff));
    }

    private void runQLearning(Problem<ERG> p) {
        //create q learning algorithm with high error
        learning = new ERGQLearning(q);
        //really run
        initTime();
        learning.run(p);
        endTime();
        //print policy found by qlearning
        Policy policy_ql = learning.getQTable().getPolicy();
        Log.info("QLearning final policy: " + p.toString(policy_ql));
    }

    private void changeQ(ERG model) {
        ERGQTable oldq = learning.getQTable();
        q = new ERGQTable(model.getStates(), model.getActions());

        for (State state : model.getStates()) {
            for (Action action : model.getActions()) {
                ERGQTableItem item = oldq.get(state, action);
                Expression exp = item.getExpression();
                double value = 0;
                try {
                    if (exp != null && !exp.getPropositions().isEmpty()) {
                        if (matchExpression(exp, avoid)) {
                            value = BAD_Q_VALUE;
                        } else if (matchExpression(exp, attract)) {
                            value = GOOD_Q_VALUE;
                        }
                    }
                } catch (Exception e) {
                }

                q.put(state, action, newItem(item, value));
            }
        }
    }

    private ERGQTableItem newItem(ERGQTableItem item, double value) {
        ERGQTableItem nitem = new ERGQTableItem(item);
        nitem.setValue(value);
        return nitem;
    }

    private boolean matchExpression(Expression stateExp, Set<Expression> exps)
            throws InvalidExpressionException {
        for (Expression exp : exps) {
            if (exp.equals(stateExp)) {
                return true;
            }
        }

        return false;
    }
//    private boolean matchAvoidExpression(Set<Proposition> propositions) throws InvalidExpressionException {
//        for (Expression exp : avoid) {
//            if (!exp.evaluate(propositions)) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    private Expression getWorstExpression(Map<Expression, Double> expsValues, Set<Expression> avoid) {
        Expression finalNewPg = null;

        while (true) {
            Expression exp = getBadExpressions(expsValues).iterator().next();
            if (!avoid.contains(exp)) {
                finalNewPg = exp;
                break;
            } else if (expsValues.isEmpty()) {
                break;
            }
            expsValues.remove(exp);
        }

        return finalNewPg;
    }

    private Set<Expression> getBadExpressions(Map<Expression, Double> expsValues) {
        MultiChooser<Expression> chooser = new MinValueChooser<Expression>();
        Set<Expression> exps = chooser.choose(expsValues);
        return exps;
    }

    private Set<Expression> getGoodExpressions(Map<Expression, Double> expsValues) {
        MultiChooser chooser = new MinValueChooser();
        return chooser.choose(expsValues);
    }
}
