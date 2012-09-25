package org.emast.model.algorithm.planning;

import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP, A extends Agent> implements PolicyGenerator<M> {

    private final List<A> agents;
    private final PolicyGenerator<M> policyGenerator;

    public Planner(PolicyGenerator<M> pPolicyGen, List<A> pAgents) {
        agents = pAgents;
        policyGenerator = pPolicyGen;
    }

    @Override
    public Policy run(Problem<M> pProblem) {
        Policy policy = policyGenerator.run(pProblem);
        //execute them all
        for (A agent : agents) {
            //set the initial policy
            agent.setPolicy(policy);
            agent.run(pProblem);
        }

        return policy;
    }

    public List<A> getIterators() {
        return agents;
    }

    public boolean isFinished() {
        boolean ret = true;
        for (A agent : agents) {
            ret &= agent.isFinished();
        }

        return ret;
    }

    public boolean existValidPlan(Problem<M> pProblem) {
        Policy policy = policyGenerator.run(pProblem);
        boolean ret = true;
        M model = pProblem.getModel();
        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final Agent iterator = new Agent(i);
            iterator.setPolicy(policy);
            //find the plan for the newly created problem
            //with the preservation goal changed
            iterator.run(pProblem);
            //get the resulting plan
            final Plan plan = iterator.getPlan();
            //save in ret if a plan was generated
            ret &= plan != null && !plan.isEmpty();
        }
        return ret;
    }

    @Override
    public String printResults() {
        return "";
    }

    public PolicyGenerator<M> getPolicyGenerator() {
        return policyGenerator;
    }
}
