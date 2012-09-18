package org.emast.model.algorithm.planning.agent.factory;

import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public interface AgentIteratorFactory<M extends MDP> {

    AgentIterator createAgentIterator(int pAgent);

    List<AgentIterator> createAgentIterators(Problem<M> problem, Policy pPolicy);
}
