package org.emast.model.algorithm.ensemble;

import java.util.*;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class AgentEnsembleICAPSSP implements Algorithm<ERG, Policy>, PolicyGenerator<ERG> {

    private final PolicyGenerator<ERG> policyGenerator;
    private List<QLearning> agentIterators;

    public AgentEnsembleICAPSSP(PolicyGenerator<ERG> pPolicyGenerator) {
        policyGenerator = pPolicyGenerator;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (QLearning agent : agentIterators) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        ERG model = pProblem.getModel();
        //start main loop
        agentIterators = new ArrayList<QLearning>(model.getAgents());
        //create initial policy
//        double init = System.currentTimeMillis();
        Policy policy = policyGenerator.run(pProblem);
//        double end = System.currentTimeMillis();
//        Log.info("\nppferg time: " + (end - init));

        for (int i = 0; i < model.getAgents(); i++) {
            final QLearning agentIterator = new QLearning();
            agentIterators.add(agentIterator);
//            init = System.currentTimeMillis();
            agentIterator.run(pProblem, policy);
//            end = System.currentTimeMillis();
//            Log.info("\nQLearning time: " + (end - init));
        }

        return policy;
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
