package org.emast.model.agent.factory;

import org.emast.model.agent.ERGAgent;
import org.emast.model.model.ERG;

/**
 *
 * @author Anderson
 */
public class ERGAgentFactory<M extends ERG> extends DefaultAgentFactory<M> {

    @Override
    public ERGAgent create(int pAgentIndex) {
        return new ERGAgent(pAgentIndex);
    }
}
