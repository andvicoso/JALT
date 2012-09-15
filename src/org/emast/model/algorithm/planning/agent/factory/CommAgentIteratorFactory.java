package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.agent.iterator.CommAgentIterator;
import org.emast.model.model.ERG;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

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
    public CommAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
        final M newModel = (M) pModel.copy();
        return new CommAgentIterator(newModel, pPolicy, pAgent, pInitialState,
                messageCost, badRewardThreshold, badMsgThreshold);
    }
}
