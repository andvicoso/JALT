package org.emast.model.agent.factory;

import org.emast.model.agent.CommChangePreservAgent;
import org.emast.model.comm.MessageManager;
import org.emast.model.model.ERG;
import org.emast.model.planning.rewardcombinator.MeanRewardCombinator;

/**
 *
 * @author Anderson
 */
public class CommChangePreservAgentFactory<M extends ERG> extends PropReputationAgentFactory<M> {

    private final double badMsgThreshold;
    private final double messageCost;
    private final MessageManager messageManager;

    public CommChangePreservAgentFactory(double pMessageCost, double pBadRewardThreshold, double pBadMsgThreshold) {
        super(pBadRewardThreshold);
        badMsgThreshold = pBadMsgThreshold;
        messageCost = pMessageCost;
        messageManager = new MessageManager(false);
    }

    @Override
    public CommChangePreservAgent create(int pAgentIndex) {
        CommChangePreservAgent a = new CommChangePreservAgent<M>(pAgentIndex, messageCost, badRewardThreshold,
                badMsgThreshold, new MeanRewardCombinator(), messageManager);
        messageManager.add(a);

        return a;
    }
}
