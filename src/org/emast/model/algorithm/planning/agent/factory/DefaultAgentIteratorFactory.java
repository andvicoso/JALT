package org.emast.model.algorithm.planning.agent.factory;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class DefaultAgentIteratorFactory<M extends MDP> implements AgentIteratorFactory<M> {

    @Override
    public List<AgentIterator> createAgentIterators(Problem<M> pProblem, Policy pInitialPolicy) {
        final List<AgentIterator> iterators = new ArrayList<AgentIterator>();
        final M model = pProblem.getModel();
        //for each agent, create an agent planner
        for (int i = 0; i < model.getAgents(); i++) {
            //create an agent iterator for each agent
            final AgentIterator ap = createAgentIterator(i);
            //save them
            iterators.add(ap);
        }

        return iterators;
    }

    @Override
    public AgentIterator createAgentIterator(int pAgent) {
        return new AgentIterator(pAgent);
    }
}
