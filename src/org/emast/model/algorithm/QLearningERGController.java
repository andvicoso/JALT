package org.emast.model.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.Utils;
import static org.emast.util.DefaultTestProperties.*;
import org.emast.util.grid.GridPrinter;

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
        MultiChooser<Expression> badChooser = new ThresholdChooser<Expression>(-15, true);
//        MultiChooser<Expression> goodChooser = new ThresholdChooser<Expression>(GOOD_EXP_VALUE, false);
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

            //2. GET GOOD AND BAD EXPRESSIONS FROM QLEARNING ITERATIONS
            Set<Expression> badExps = getExpressions(learning.getQTable().getExpsValues(),
                    badChooser, avoid);
//            Set<Expression> goodExps = getExpressions(learning.getQTable().getExpsValues(),
//                    goodChooser, attract);

            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSIONS
            if (!badExps.isEmpty()) {
                avoid.addAll(badExps);
                System.out.println("Avoid: " + avoid);

//                attract.addAll(goodExps);
//                System.out.println("Attract: " + attract);

                changeQ(model);
                System.out.println("QTable: \n" + q.toString());
            } else {
                break;
            }
        } while (iterations < MAX_IT);

        //4. CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
        //AND JOIN IT WITH THE CURRENT
        Expression badExp = new Expression(BinaryOperator.OR, avoid.toArray(new Expression[avoid.size()]));
        model.getPreservationGoal().add(badExp.parenthesize().negate(), BinaryOperator.AND);

        //5. CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        TransitionFunction t = ERGFactory.createTransitionFunctionFrequency(q);
        model.setTransitionFunction(t);

        Log.info("\n" + new GridPrinter().print(t, model));

        //6. RUN PPFERG FOR THE FINAL PRESERV GOAL AND TRANSITION FUNCTION
        //AND GET THE FINAL POLICY!
        final PPFERG ppferg = new PPFERG();
        return ppferg.run(p);
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

    private Set<Expression> getExpressions(Map<Expression, Double> expsValues, MultiChooser<Expression> chooser,
            Set<Expression> container) {
        Set<Expression> exps = chooser.choose(expsValues);

        exps.removeAll(container);

        return exps;
    }
}
