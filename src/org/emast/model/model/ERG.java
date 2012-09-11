package org.emast.model.model;

import org.emast.model.propositional.Expression;

/**
 *
 * @author Anderson
 */
public interface ERG extends SRG {

    Expression getPreservationGoal();

    void setPreservationGoal(Expression pPreservationGoal);
}
