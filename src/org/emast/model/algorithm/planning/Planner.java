package org.emast.model.algorithm.planning;

import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP, A extends AgentIterator> {

    private final List<A> agents;
    private final PolicyGenerator<M> policyGenerator;

    public Planner(PolicyGenerator<M> pPolicyGen, List<A> pAgents) {
        agents = pAgents;
        policyGenerator = pPolicyGen;
    }

    public void run(final Problem<M> pProblem) {
        final Policy policy = policyGenerator.run(pProblem);
        //execute them all
        for (final A agent : agents) {
            //set the initial policy
            agent.setPolicy(policy);
            agent.run(pProblem);
        }
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
}
