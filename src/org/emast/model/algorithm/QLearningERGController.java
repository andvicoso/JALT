package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.Utils;
import static org.emast.util.DefaultTestProperties.*;
/**
 *
 * @author Anderson
 */
public class QLearningERGController extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 2;
    
    private long init;
    private long end;
    private ERGQTable q;
    private ERGQLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();
        Expression newPreservGoal;
        
        findBestPlan(pProblem);

        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            //1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p);

            //2. GET NEW PRESERVATION GOAL FROM QLEARNING ITERATIONS
            newPreservGoal = ERGFactory.createPresevationGoal(model, learning.getQTable().getExpsValues());

            //3. REDUCE Q FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            if (newPreservGoal != null) {
                lowerQ(model, newPreservGoal);
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

    private void lowerQ(ERG model, Expression newPreservGoal) {
        ERGQTable oldq = learning.getQTable();
        q = new ERGQTable(model.getStates(), model.getActions());

        for (State state : model.getStates()) {
            for (Action action : model.getActions()) {
                ERGQTableItem item = oldq.get(state, action);
                Expression exp = item.getExpression();
                double value = 0;
                try {
                    if (exp != null && !newPreservGoal.evaluate(exp.getPropositions())) {
                        value = BAD_Q_VALUE;
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
}
