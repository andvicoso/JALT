package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.ERGFactory;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class QLearningERGController extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 2;
    private QLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            learning = new QLearning();
            learning.run(p);
            
            Log.info("\nQTable: \n" + learning.getQTable());
            Log.info("\nFTable: \n" + learning.getFrequencyTable());

            model = ERGFactory.create(model, learning);
            p = new Problem<ERG>(model, pProblem.getInitialStates());
            
            PPFERG ppferg = new PPFERG();
            Policy policy = ppferg.run(p);
            Log.info("\nPolicy: \n" + policy.getBestPolicy().toString());
        } while (iterations < MAX_IT);

        return learning.getQTable().getPolicy(false);
    }

    @Override
    public String printResults() {
        return learning.printResults();
    }
}
