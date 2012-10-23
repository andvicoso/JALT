package org.emast.model.agent.combineresults;

import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.agent.behaviour.CollectiveBehaviour;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface CombineResults<M extends MDP> extends CollectiveBehaviour {

    void combine(M pModel, List<Agent> pAgents);
}
