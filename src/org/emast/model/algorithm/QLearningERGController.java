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
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
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
    private ERGQLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        MultiChooser<Expression> badChooser = new ThresholdChooser<Expression>(-15, true);
        Set<Expression> avoid = new HashSet<Expression>();
        //int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();

        // findBestPlan(pProblem);

        //start main loop
        //do {
        //Log.info("\nITERATION " + iterations++ + ":\n");

        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());

        //1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING) 
        runQLearning(p, q);

        //2. GET BAD EXPRESSIONS FROM QLEARNING ITERATIONS
        Set<Expression> badExps = badChooser.choose(q.getExpsValues());

        //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
        // WHICH HAVE THE FOUND EXPRESSIONS
        if (!badExps.isEmpty()) {
            avoid.addAll(badExps);
            System.out.println("Avoid: " + avoid);

            updateQ(model, q, avoid);
            System.out.println("QTable: \n" + q.toString());
        }

        //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
        model = createModel(model, q, avoid);
        p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());

        //} while (iterations < MAX_IT);

        //5. RUN PPFERG FOR THE NEW MODEL
        final PPFERG ppferg = new PPFERG();

        //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
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

    private void runQLearning(Problem<ERG> p, ERGQTable q) {
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

    private ERGQTable updateQ(ERG model, ERGQTable q, Set<Expression> avoid) {
        ERGQTable oldq = learning.getQTable();

        for (State state : model.getStates()) {
            for (Action action : model.getActions()) {
                ERGQTableItem item = oldq.get(state, action);
                Expression exp = item.getExpression();
                double value = 0;
                try {
                    if (exp != null && !exp.getPropositions().isEmpty()) {
                        if (matchExpression(exp, avoid)) {
                            value = BAD_Q_VALUE;
                        }
                    }
                } catch (Exception e) {
                }

                q.put(state, action, newItem(item, value));
            }
        }

        return q;
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

    private ERG createModel(ERG oldModel, ERGQTable q, Set<Expression> avoid) {
        ERGModel model = new ERGModel();
        //COPY MAIN PROPERTIES
        model.setStates(q.getStates());
        model.setActions(q.getActions());
        model.setGoal(oldModel.getGoal());
        model.setAgents(oldModel.getAgents());
        //GET THE SET OF PROPOSITIONS FROM EXPLORATED STATES
        model.setPropositions(getPropositions(q.getExpsValues()));
        //CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
        Expression badExp = new Expression(BinaryOperator.OR, avoid.toArray(new Expression[avoid.size()]));
        model.setPreservationGoal(badExp.parenthesize().negate());
        //CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        TransitionFunction tf = ERGFactory.createTransitionFunctionFrequency(q);
        model.setTransitionFunction(tf);
        //CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        PropositionFunction pf = ERGFactory.createPropositionFunction(q);
        model.setPropositionFunction(pf);
        //CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        RewardFunction rf = ERGFactory.createRewardFunction(q);
        model.setRewardFunction(rf);

        Log.info("\n" + new GridPrinter().print(tf, model));

        return model;
    }

    private Set<Proposition> getPropositions(Map<Expression, Double> expsValues) {
        Set<Proposition> props = new HashSet<Proposition>();
        for (Expression exp : expsValues.keySet()) {
            Set<Proposition> expProps = exp.getPropositions();
            props.addAll(expProps);
        }

        return props;
    }
}
