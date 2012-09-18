package org.emast.model.exception;

/**
 *
 * @author Anderson
 */
public class InvalidExpressionException extends Exception {

    public InvalidExpressionException(String pExpression) {
        super(pExpression);
    }
}
