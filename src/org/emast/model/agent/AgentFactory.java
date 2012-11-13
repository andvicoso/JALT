package org.emast.model.agent;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public class AgentFactory<M extends MDP> {

    public List<ERGAgentIterator> createAgents(int pAgents, List<Individual> pBehaviors) {
        final List<ERGAgentIterator> agents = new ArrayList<ERGAgentIterator>();
        //for each agent, create an agent planner
        for (int i = 0; i < pAgents; i++) {
            //create an agent iterator for each agent
            final ERGAgentIterator ap = new ERGAgentIterator(i, pBehaviors);
            //save them
            agents.add(ap);
        }

        return agents;
    }
}
