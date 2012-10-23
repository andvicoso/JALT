package org.emast.model.agent;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.agent.behaviour.Behaviour;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public class AgentFactory<M extends MDP> {

    public List<Agent> createAgents(int pAgents, List<Behaviour> pBehaviours) {
        final List<Agent> agents = new ArrayList<Agent>();
        //for each agent, create an agent planner
        for (int i = 0; i < pAgents; i++) {
            //create an agent iterator for each agent
            final Agent ap = new Agent(i, pBehaviours);
            //save them
            agents.add(ap);
        }

        return agents;
    }
}
