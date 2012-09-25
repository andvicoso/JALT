package org.emast.model.agent.factory;

import org.emast.model.agent.PropReputationAgent;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class PropReputationAgentFactory<M extends ERG> extends ERGAgentFactory<M> {

    protected double badRewardThreshold;

    public PropReputationAgentFactory(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public PropReputationAgent createAgentIterator(int pAgent) {
        return new PropReputationAgent(pAgent, badRewardThreshold);
    }
}