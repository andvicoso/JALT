package org.emast.model.planning;

import org.emast.model.agent.AgentIteration;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class ValidPathFinder {

    public static <M extends MDP> boolean exist(Problem<M> pProblem, PolicyGenerator<M> pPolicyGenerator,
            boolean pAcceptOne) {
        boolean ret = true;
        Policy policy = pPolicyGenerator.run(pProblem);
        MDP model = pProblem.getModel();

        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final AgentIteration agent = new AgentIteration(i);
            //find the plan for the newly created agent
            agent.run(pProblem, policy);
            //get the resulting plan
            final Plan plan = agent.getPlan();
            //save in ret if a plan was generated
            ret &= plan != null && !plan.isEmpty();
            //if found plan and need at least one plan, return true
            if (ret && pAcceptOne) {
                break;
            }
        }
        return ret;
    }

    public static boolean exist(Problem pProblem, Policy pPolicy, int pAgent) {
        //create a new simple agent iterator
        final AgentIteration agent = new AgentIteration(pAgent);
        //find the plan for the newly created problem
        //with the preservation goal changed
        agent.run(pProblem, pPolicy);
        //get the resulting plan
        final Plan plan = agent.getPlan();
        return plan != null && !plan.isEmpty();
    }

    public static <M extends MDP> boolean exist(Problem<M> pProblem, PolicyGenerator<M> pPolicyGenerator,
            int pAgent) {
        Policy policy = pPolicyGenerator.run(pProblem);
        return exist(pProblem, policy, pAgent);
    }
}
