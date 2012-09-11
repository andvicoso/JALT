package org.emast.model.propositional.operator;

/**
 *
 * @author Anderson
 */
public class UnaryOperator extends Operator {

    public static final UnaryOperator NOT = new UnaryOperator("!");

    protected UnaryOperator(final String pToken) {
        super(pToken);
    }

    public boolean evaluate(final boolean a) {
        return !a;
    }
}
