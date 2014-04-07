package org.jalt.model.model;

import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;

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
