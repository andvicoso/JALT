package org.emast.model.model;

import java.util.Set;
import org.emast.model.function.PropositionFunction;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author andvicoso
 */
public interface SRG extends MDP {

    void setPropositionFunction(PropositionFunction pf);

    PropositionFunction getPropositionFunction();

    void setPropositions(Set<Proposition> props);

    Set<Proposition> getPropositions();

    Expression getGoal();

    void setGoal(Expression pGoal);
}
