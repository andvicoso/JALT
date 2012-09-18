package org.emast.model.algorithm.planning;

import java.util.List;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmThread;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP, A extends AgentIterator> implements Algorithm<M, Policy> {

    private final List<A> agents;

    public Planner(List<A> pAgents) {
        agents = pAgents;
    }

    @Override
    public Policy run(Problem<M> pProblem) {
        //execute them all
        for (final A agentPlanner : agents) {
            new AlgorithmThread(agentPlanner, pProblem).start();
        }

        return null; //TODO: combine?
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

    @Override
    public String printResults() {
        return "";
    }
}
