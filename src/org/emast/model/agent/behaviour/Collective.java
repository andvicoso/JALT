package org.emast.model.agent.behaviour;

import java.util.List;
import java.util.Map;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Collective<M extends MDP> extends Behaviour {

    void behave(List<Agent> pAgents, Problem<M> pProblem, Map<String, Object> pParameters);
}
