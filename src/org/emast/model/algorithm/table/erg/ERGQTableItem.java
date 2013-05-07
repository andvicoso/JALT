package org.emast.model.algorithm.table.erg;

import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
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
