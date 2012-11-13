package org.emast.model.agent.behavior;

import java.util.List;
import java.util.Map;
import org.emast.model.agent.AgentIteration;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Collective<M extends MDP> extends Behavior {

    void behave(List<AgentIteration> pAgents, Problem<M> pProblem, Map<String, Object> pParameters);
}
