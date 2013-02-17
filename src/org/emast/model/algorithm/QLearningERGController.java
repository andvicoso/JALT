package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class QLearningERGController extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 2;
    private ERGQLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            learning = new ERGQLearning();
            Policy policy_ql = learning.run(p);

            Log.info("\nQTable: \n" + learning.getQTable());
            Log.info("\nPolicy QLearning: \n" + policy_ql.getBestPolicy().toString());
            Log.info("\n" + pProblem.toString(policy_ql));

            model = ERGFactory.create(model, learning);
            if (model != null) {
                p = new Problem<ERG>(model, pProblem.getInitialStates());

                PPFERG ppferg = new PPFERG();
                Policy policy_ppferg = ppferg.run(p);

                TransitionFunction tf = policy_ppferg.createTransitionFunction(learning.getQTable());
                model.setTransitionFunction(tf);

                Log.info("\nPolicy PPFERG: \n" + policy_ppferg.getBestPolicy().toString());
                Log.info("\n" + pProblem.toString(policy_ppferg));
            }
        } while (model != null && iterations < MAX_IT);

        return learning.getQTable().getPolicy(false);
    }

    @Override
    public String printResults() {
        return learning.printResults();
    }
}
