package org.emast.model.agent.behavior.individual.reward;

import org.emast.model.agent.behavior.Individual;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public interface RewardBehavior<M extends MDP, R> extends Individual<M> {

    R getResult();
}
