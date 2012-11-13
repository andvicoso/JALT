package org.emast.model.agent.behavior;

import java.util.Map;
import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface Individual<M extends MDP> extends Behavior {

    void behave(ERGAgentIterator pAgent, Problem<M> pProblem, Map<String, Object> pParameters);
}
