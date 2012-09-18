package org.emast.model.algorithm.planning.agent.factory;

import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface AgentIteratorFactory<M extends MDP> {

    AgentIterator createAgentIterator(int pAgent);

    List<AgentIterator> createAgentIterators(int pAgents);
}
