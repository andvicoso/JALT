package org.emast.model.model;

import java.util.Set;
import org.emast.model.function.PropositionFunction;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface SRG extends MDP{

    void setPropositionFunction(PropositionFunction pf);

    void setPropositions(Set<Proposition> props);

    PropositionFunction getPropositionFunction();

    Set<Proposition> getPropositions();

    Expression getGoal();

    void setGoal(Expression pGoal);
}
