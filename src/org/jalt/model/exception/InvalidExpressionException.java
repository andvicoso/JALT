package org.jalt.model.exception;

/**
 *
 * @author andvicoso
 */
public class InvalidExpressionException extends Exception {

    public InvalidExpressionException(String pExpression) {
        super(pExpression);
    }
}
