package org.emast.model.agent.behaviour;

import java.util.Map;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Individual<M extends MDP> extends Behaviour {

    void behave(Agent pAgent, Problem<M> pProblem, Map<String, Object> pParameters);
}
