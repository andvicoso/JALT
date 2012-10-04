package org.emast.model.agent.factory;

import org.emast.model.agent.CommAgent;
import org.emast.model.algorithm.planning.rewardcombinator.MeanRewardCombinator;
import org.emast.model.comm.MessageManager;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class CommAgentFactory<M extends ERG> extends PropReputationAgentFactory<M> {

    private final double badMsgThreshold;
    private final double messageCost;
    private final MessageManager messageManager;

    public CommAgentFactory(double pMessageCost, double pBadRewardThreshold, double pBadMsgThreshold) {
        super(pBadRewardThreshold);
        badMsgThreshold = pBadMsgThreshold;
        messageCost = pMessageCost;
        messageManager = new MessageManager(false);
    }

    @Override
    public CommAgent create(int pAgentIndex) {
        CommAgent a = new CommAgent(pAgentIndex, messageCost, badRewardThreshold,
                badMsgThreshold, new MeanRewardCombinator(), messageManager);
        messageManager.add(a);

        return a;
    }
}
