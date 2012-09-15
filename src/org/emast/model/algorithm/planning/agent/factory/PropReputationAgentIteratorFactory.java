package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

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
    public PropReputationAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
        final M newModel = (M) pModel.copy();
        return new PropReputationAgentIterator(newModel, pPolicy, pAgent, pInitialState, badRewardThreshold);
    }
}