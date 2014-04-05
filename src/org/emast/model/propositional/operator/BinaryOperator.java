package org.emast.model.propositional.operator;

/**
 *
 * @author andvicoso
 */
public class BinaryOperator extends Operator {

    public static final BinaryOperator AND = new BinaryOperator("&");
    public static final BinaryOperator OR = new BinaryOperator("|");
    public static final BinaryOperator[] OPERATORS = new BinaryOperator[]{AND, OR};

    protected BinaryOperator(final String pToken) {
        super(pToken);
    }

    public boolean evaluate(final boolean a, final boolean b) {
        return equals(AND) ? a && b : a || b;
    }

    @Override
    public String toString() {
        return getToken();
    }
}
