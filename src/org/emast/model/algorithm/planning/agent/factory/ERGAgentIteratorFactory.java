package org.emast.model.algorithm.planning.agent.factory;

import org.emast.model.algorithm.planning.agent.iterator.ERGAgentIterator;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class ERGAgentIteratorFactory<M extends ERG> extends DefaultAgentIteratorFactory<M> {

    @Override
    public ERGAgentIterator createAgentIterator(int pAgent) {
        return new ERGAgentIterator(pAgent);
    }
}
