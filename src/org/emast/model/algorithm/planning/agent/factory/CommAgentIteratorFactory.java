package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.rewardcombinator.impl.MeanRewardCombinator;
import org.emast.model.algorithm.planning.agent.iterator.CommAgentIterator;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class CommAgentIteratorFactory<M extends ERG> extends PropReputationAgentIteratorFactory<M> {

    protected double badMsgThreshold;
    private final double messageCost;

    public CommAgentIteratorFactory(double pMessageCost, double pBadRewardThreshold, double pBadMsgThreshold) {
        super(pBadRewardThreshold);
        badMsgThreshold = pBadMsgThreshold;
        messageCost = pMessageCost;
    }

    @Override
    public CommAgentIterator createAgentIterator(int pAgent) {
        return new CommAgentIterator(pAgent, messageCost, badRewardThreshold,
                badMsgThreshold, new MeanRewardCombinator());
    }
}
