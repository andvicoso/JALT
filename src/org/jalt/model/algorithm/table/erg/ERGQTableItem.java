package org.jalt.model.algorithm.table.erg;

import org.jalt.model.algorithm.table.QTableItem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class ERGQTableItem extends QTableItem {

    /**
     * expression correspondent to nextState features
     */
    private Expression expression;

    public ERGQTableItem() {
    }

    public ERGQTableItem(Double value, Double reward, Integer freq, State finalState, Expression expression) {
        super(value, reward, freq, finalState);
        this.expression = expression;
    }

    public ERGQTableItem(ERGQTableItem item) {
        super(item);
        this.expression = item.expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
