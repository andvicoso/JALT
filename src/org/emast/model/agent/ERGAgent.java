package org.emast.model.agent;

import java.util.Collection;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGAgent<M extends ERG> extends Agent<M> {

    public ERGAgent(int pAgent) {
        super(pAgent);
    }

    protected Collection<Proposition> getPropositionsForState(final State pState) {
        return model.getPropositionFunction().getPropositionsForState(pState);
    }

    protected PPFERG<M> getAlgorithm() {
        return new PPFERG<M>();
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder(super.printResults());
        sb.append("\nFinal goal: ").append(model.getGoal());
        sb.append("\nPreservation goal: ").append(model.getPreservationGoal());

        return sb.toString();
    }
}
