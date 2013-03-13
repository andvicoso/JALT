package org.emast.model.planning;

import java.util.Collection;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;

/**
 *
 * @author Anderson
 */
public class PreservationGoalFactory {

    public Expression createPreservationGoalExp(Expression pOriginalPreservGoal, Collection<Expression> pExpressions) {
        Expression result = new Expression(pOriginalPreservGoal);
        for (Expression e : pExpressions) {
            //negate it
            Expression neg = e.negate();
            //add to the returned exp
            result.add(neg, BinaryOperator.AND);
        }
        return result;
    }

    public Expression createPreservationGoal(Expression pOriginalPreservGoal, Collection<Proposition> pProps) {
        Expression exp = new Expression(pOriginalPreservGoal);
        for (Proposition prop : pProps) {
            //create expression for each bad reward proposition
            Expression e = new Expression(BinaryOperator.AND, prop);
            //negate it
            e = e.negate();
            //add to the returned exp
            exp.add(e, BinaryOperator.AND);
        }
        return exp;
    }
}
