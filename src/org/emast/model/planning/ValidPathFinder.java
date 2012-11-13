package org.emast.model.planning;

import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.agent.AgentIterator;
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

    public static <M extends MDP> boolean exist(Problem<M> pProblem, Policy pPolicy,
            boolean pAcceptOne) {
        boolean ret = true;
        MDP model = pProblem.getModel();

        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final AgentIterator agent = new AgentIterator<M>(i);
            agent.setDebug(false);
            //find the plan for the newly created agent
            agent.run(pProblem, pPolicy);
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

    public static <M extends MDP> boolean exist(Problem<M> pProblem, PolicyGenerator<M> pPolicyGenerator,
            boolean pAcceptOne) {
        return exist(pProblem, pPolicyGenerator.run(pProblem), pAcceptOne);
    }

    public static boolean exist(Problem pProblem, Policy pPolicy, int pAgent) {
        //create a new simple agent iterator
        final ERGAgentIterator agent = new ERGAgentIterator(pAgent);
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
