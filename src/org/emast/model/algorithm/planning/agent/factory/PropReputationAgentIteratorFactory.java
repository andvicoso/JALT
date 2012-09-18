package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class PropReputationAgentIteratorFactory<M extends ERG> extends ERGAgentIteratorFactory<M> {

    protected double badRewardThreshold;

    public PropReputationAgentIteratorFactory(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public PropReputationAgentIterator createAgentIterator(int pAgent) {
        return new PropReputationAgentIterator( pAgent, badRewardThreshold);
    }
}