package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SimplePolicy;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class QLearningERGController extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 2;
    private ERGQLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        PPFERG ppferg = new PPFERG();
        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();

        findBestPlan(pProblem);
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            learning = new ERGQLearning();
            learning.run(p);
            SimplePolicy policy_ql = learning.getQTable().getPolicy();

            //Log.info("\nQTable: \n" + learning.getQTable());
            //Log.info("\nPolicy QLearning: \n" + policy_ql.toString());
            Log.info("\nQLearning" + pProblem.toString(policy_ql));
            //Log.info("\n" + new GridPrinter().toTable(learning.getQTable().getFrequencyTableStr()));

            //ai.run(pProblem, policy_ql);
            //Log.info("\nTotal reward value: " + ai.getTotalReward());

            model = ERGFactory.create(model, learning);

            //break if model could not be created
            if (model != null) {
                Log.info("\n" + new GridPrinter().print(model.getTransitionFunction(), model));
                p = new Problem<ERG>(model, pProblem.getInitialStates());

                Policy policy_ppferg = ppferg.run(p);

                TransitionFunction tf = policy_ppferg.createTransitionFunction(learning.getQTable(),
                        model.getTransitionFunction(), model);
                model.setTransitionFunction(tf);

                Log.info("\n" + new GridPrinter().print(tf, model));

                //Log.info("\nPolicy PPFERG: \n" + policy_ppferg.getBestPolicy().toString());
                Log.info("\nPPFERG" + pProblem.toString(policy_ppferg));
                findBestPlan(p);
            }
        } while (model != null && iterations < MAX_IT);

        return new Policy();//learning.getQTable().getPolicy(false);//TODO
    }

    @Override
    public String printResults() {
        return learning.printResults();
    }

    private void findBestPlan(Problem<ERG> pProblem) {
        ERG model = pProblem.getModel();
        ValueIteration vi = new ValueIteration();
        Policy pi = vi.run(pProblem, (Object) null);

        //Log.info("\n" + pProblem.toString(pi));

        State st = pProblem.getInitialStates().get(0);
        Plan plan = new Plan();
        double sum = 0;
        do {
            Action a = pi.getBestAction(st);
            sum += model.getRewardFunction().getValue(st, a);
            st = model.getTransitionFunction().getBestReachableState(model.getStates(), st, a);
            plan.add(a);
        } while (st != null && !pProblem.getFinalStates().contains(st));

        Log.info("Value Iteration its: " + vi.getIterations());
        Log.info("Best plan: " + plan);
        Log.info("Best plan reward value: " + sum);
    }
}
