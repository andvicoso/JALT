package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.agent.iterator.ERGAgentIterator;
import org.emast.model.model.ERG;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGAgentIteratorFactory<M extends ERG> extends DefaultAgentIteratorFactory<M> {

    @Override
    public ERGAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
        final M newModel = (M) pModel.copy();
        return new ERGAgentIterator(newModel, pPolicy, pAgent, pInitialState);
    }
}
