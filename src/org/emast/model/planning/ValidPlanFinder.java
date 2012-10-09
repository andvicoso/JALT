package org.emast.model.planning;

import org.emast.model.agent.Agent;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class ValidPlanFinder {

    public static boolean exist(Problem pProblem, PolicyGenerator<? extends MDP> pPolicyGenerator) {
        Policy policy = pPolicyGenerator.run(pProblem);
        boolean ret = true;
        MDP model = pProblem.getModel();
        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final Agent agent = new Agent(i);
            agent.init(pProblem, policy);
            //find the plan for the newly created problem
            //with the preservation goal changed
            agent.run(pProblem);
            //get the resulting plan
            final Plan plan = agent.getPlan();
            //save in ret if a plan was generated
            ret &= plan != null && !plan.isEmpty();
        }
        return ret;
    }

    public static boolean exist(Problem pProblem, Policy pPolicy, int pAgent) {
        //create a new simple agent iterator
        final Agent agent = new Agent(pAgent);
        agent.init(pProblem, pPolicy);
        //find the plan for the newly created problem
        //with the preservation goal changed
        agent.run(pProblem);
        //get the resulting plan
        final Plan plan = agent.getPlan();
        return plan != null && !plan.isEmpty();
    }

    public static boolean exist(Problem pProblem, PolicyGenerator<? extends MDP> pPolicyGenerator, int pAgent) {
        Policy policy = pPolicyGenerator.run(pProblem);
        return exist(pProblem, policy, pAgent);
    }
}
