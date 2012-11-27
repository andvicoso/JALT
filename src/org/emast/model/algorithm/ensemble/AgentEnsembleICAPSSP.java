package org.emast.model.algorithm.ensemble;

import java.util.*;
import org.emast.model.agent.ERGQLearning;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class AgentEnsembleICAPSSP extends DefaultAlgorithm<ERG, Policy>
        implements PolicyGenerator<ERG> {

    private final PolicyGenerator<ERG> policyGenerator;
    private List<ERGQLearning> agentIterators;

    public AgentEnsembleICAPSSP(PolicyGenerator<ERG> pPolicyGenerator) {
        policyGenerator = pPolicyGenerator;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (ERGQLearning agent : agentIterators) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        ERG model = pProblem.getModel();
        //start main loop
        agentIterators = new ArrayList<ERGQLearning>(model.getAgents());
        //create initial policy
//        double init = System.currentTimeMillis();
        Policy policy = policyGenerator.run(pProblem);
//        double end = System.currentTimeMillis();
//        System.out.println("ppferg time: " + (end - init));

        for (int i = 0; i < model.getAgents(); i++) {
            final ERGQLearning agentIterator = new ERGQLearning();
            agentIterators.add(agentIterator);
//            init = System.currentTimeMillis();
            agentIterator.run(pProblem, policy);
//            end = System.currentTimeMillis();
//            System.out.println("ERGQLearning time: " + (end - init));
        }

        return policy;
    }
}
