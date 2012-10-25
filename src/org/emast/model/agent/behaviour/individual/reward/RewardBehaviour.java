package org.emast.model.agent.behaviour.individual.reward;

import org.emast.model.agent.behaviour.Individual;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface RewardBehaviour<M extends MDP, R> extends Individual<M> {

    R getResult();
}
