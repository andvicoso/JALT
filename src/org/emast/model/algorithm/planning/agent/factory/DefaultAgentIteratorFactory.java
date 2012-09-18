package org.emast.model.algorithm.planning.agent.factory;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public class DefaultAgentIteratorFactory<M extends MDP> implements AgentIteratorFactory<M> {

    @Override
    public List<AgentIterator> createAgentIterators(int pAgents) {
        final List<AgentIterator> iterators = new ArrayList<AgentIterator>();
        //for each agent, create an agent planner
        for (int i = 0; i < pAgents; i++) {
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
