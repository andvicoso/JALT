package org.emast.model.algorithm.planning.agent.iterator;

import java.util.Collection;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGAgentIterator<M extends ERG> extends AgentIterator<M> {

    public ERGAgentIterator(M pModel, Policy pInitialPolicy, int pAgent, State pInitialState) {
        super(pModel, pInitialPolicy, pAgent, pInitialState);
    }

    protected Collection<Proposition> getPropositionsForState(final State pState) {
        return getModel().getPropositionFunction().getPropositionsForState(pState);
    }

    protected PPFERG<M> getAlgorithm() {
        return new PPFERG<M>();
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nFinal goal: ").append(getModel().getGoal());
        sb.append("\nPreservation goal: ").append(getModel().getPreservationGoal());

        return sb.toString();
    }
}
