package org.emast.model.agent.factory;

import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface AgentFactory<M extends MDP> {

    <A extends Agent> A create(int pAgentIndex);

    <A extends Agent> List<A> createAgents(int pAgents);
}
