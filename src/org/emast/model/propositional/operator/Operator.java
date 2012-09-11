package org.emast.model.propositional.operator;

public abstract class Operator {

    private String token;

    protected Operator(final String pOp) {
        token = pOp;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token;
    }
}
