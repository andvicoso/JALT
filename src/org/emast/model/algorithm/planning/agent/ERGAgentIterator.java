package org.emast.model.algorithm.planning.agent;

import java.util.Collection;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGAgentIterator<M extends MDP & ERG> extends AgentIterator<M> {

    public ERGAgentIterator(M pModel, Policy pInitialPolicy, int pAgent, State pInitialState) {
        super(pModel, pInitialPolicy, pAgent, pInitialState);
    }

    protected Collection<Proposition> getPropositionsForState(final State pState) {
        return getModel().getPropositionFunction().getPropositionsForState(pState);
    }

    protected PPFERG<M> getAlgorithm() {
        return new PPFERG<M>();
    }

    public static class ERGAgentIteratorFactory<M extends MDP & ERG> extends DefaultAgentIteratorFactory<M> {

        @Override
        public ERGAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
            final M newModel = (M)pModel.copy();
            return new ERGAgentIterator(newModel, pPolicy, pAgent, pInitialState);
        }
    }
}
