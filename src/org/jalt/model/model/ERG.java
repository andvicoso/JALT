package org.jalt.model.model;

import org.jalt.model.propositional.Expression;

/**
 *
 * @author andvicoso
 */
public interface ERG extends SRG {

    Expression getPreservationGoal();

    void setPreservationGoal(Expression pPreservationGoal);
}
