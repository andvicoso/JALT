package org.emast.model.algorithm.planning.agent.factory;

import java.util.List;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public interface AgentIteratorFactory<M extends MDP> {

    AgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState);

    List<AgentIterator> createAgentIterators(Problem<M> problem, Policy pPolicy);
}
