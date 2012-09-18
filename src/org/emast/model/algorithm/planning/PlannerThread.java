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
public class PlannerThread<M extends MDP, A extends AgentIterator> {

    private final List<A> agents;
    private final PolicyGenerator<M> policyGenerator;

    public PlannerThread(PolicyGenerator<M> pPolicyGen, List<A> pAgents) {
        agents = pAgents;
        policyGenerator = pPolicyGen;
    }

    public void run(final Problem<M> pProblem) {
        final Policy policy = policyGenerator.run(pProblem);
        //execute them all
        for (final A agent : agents) {
            //set the initial policy
            agent.setPolicy(policy);
            //get new thread name
            String threadName = agent.getClass().getSimpleName()
                    + " - " + pProblem.getClass().getSimpleName();
            //create and run an thread for the agent iterator 
            new Thread(new Runnable() {
                @Override
                public void run() {
                    agent.run(pProblem);

                    finished(agent);
                }
            }, threadName).start();
        }
    }

    protected void finished(A agent) {
        if (isFinished()) {
            finished();
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

    protected void finished() {
    }
}
