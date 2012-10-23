package org.emast.model.agent.behaviour.individual.reward;

import org.emast.model.agent.behaviour.IndividualBehaviour;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface RewardBehaviour<M extends MDP, R> extends IndividualBehaviour<M> {

    R getResult();
}
