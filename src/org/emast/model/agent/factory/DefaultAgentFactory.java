package org.emast.model.agent.factory;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public class DefaultAgentFactory<M extends MDP> implements AgentFactory<M> {

    @Override
    public List<Agent> createAgentIterators(int pAgents) {
        final List<Agent> iterators = new ArrayList<Agent>();
        //for each agent, create an agent planner
        for (int i = 0; i < pAgents; i++) {
            //create an agent iterator for each agent
            final Agent ap = createAgentIterator(i);
            //save them
            iterators.add(ap);
        }

        return iterators;
    }

    @Override
    public Agent createAgentIterator(int pAgent) {
        return new Agent(pAgent);
    }
}
