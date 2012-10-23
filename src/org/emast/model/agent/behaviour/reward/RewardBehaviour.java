package org.emast.model.agent.behaviour.reward;

import org.emast.model.agent.Agent;
import org.emast.model.agent.behaviour.IndividualBehaviour;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public interface RewardBehaviour<M extends MDP, R> extends IndividualBehaviour {

    void manageReward(Agent pAgent, M pModel, State pNextState, double pReward);

    R getResult();
}
