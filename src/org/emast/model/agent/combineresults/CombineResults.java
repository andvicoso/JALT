package org.emast.model.agent.combineresults;

import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public interface CombineResults<M extends MDP, A extends Agent> {

    void combine(Problem<M> pProblem, List<A> pAgents);
}
