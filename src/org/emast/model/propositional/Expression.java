package org.emast.model.propositional;

import java.io.Serializable;
import java.util.*;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.propositional.operator.BinaryOperator;
import static org.emast.model.propositional.operator.BinaryOperator.AND;
import static org.emast.model.propositional.operator.BinaryOperator.OR;
import static org.emast.model.propositional.operator.UnaryOperator.NOT;

/**
 *
 * @author And
 */
public final class Expression implements Serializable {

    public static final Expression EMPTY = new Expression("");
    private static final String JEVAL_AND_TOKEN = "&&";
    private static final String JEVAL_OR_TOKEN = "||";
    private static final String JEVAL_TRUE_VALUE = "1.0";
    private static final String JEVAL_FALSE_VALUE = "0.0";
    private static final String VARIABLES_DELIMS = " &|!()";
    private static final String EXPS_DELIMS = "&|";
    private static final String EVALUATE_DELIMS = " !()";
    private String expression;

    public Expression() {
        this("");
    }

    public Expression(final String pExpressionText) {
        expression = pExpressionText;
        optimize();
    }

    public Expression(Expression pExpression) {
        this(pExpression.expression);
    }

    public Expression(final Proposition pProposition) {
        this(pProposition.getName());
    }

    public Expression(final BinaryOperator pGlueOperator, final Expression... pExpressions) {
        for (final Expression exp : pExpressions) {
            add(exp, pGlueOperator);
        }
        optimize();
    }

    public Expression(final BinaryOperator pGlueOperator, final Collection<Proposition> pPropositions) {
        for (final Proposition proposition : pPropositions) {
            add(new Expression(proposition), pGlueOperator);
        }
        optimize();
    }

    public Expression(final BinaryOperator pGlueOperator, final Proposition... pPropositions) {
        this(pGlueOperator, Arrays.asList(pPropositions));
    }

    public static boolean isNegated(final String exp) {
        return exp.startsWith(NOT.toString() + "(")
                || (exp.startsWith(NOT.toString()) && isPrimitive(exp));
    }

    public static boolean isPrimitive(final String exp) {
        final String[] invalidChars = {AND.toString(), OR.toString()};
        for (final String string : invalidChars) {
            if (exp.contains(string)) {
                return false;
            }
        }

        return true;
    }

    public boolean isNegated() {
        return isNegated(expression);
    }

    public Expression negate() {
        String exp = expression;
        if (!isPrimitive() && !isParenthesized()) {
            exp = parenthesize();
        }
        if (isNegated()) {
            return new Expression(exp.substring(1));
        }
        return new Expression(NOT + exp);
    }

    public boolean isEmpty() {
        return expression.isEmpty();
    }

    public boolean evaluate(final Set<Proposition> pTrueProps)
            throws InvalidExpressionException {
        if (pTrueProps == null) {
            return false;
        }
        //create propositions values for expression
        Set<Proposition> expProps = getPropositions();
        Map<String, String> map = new HashMap<String, String>(expProps.size());

        for (final Proposition proposition : expProps) {
            boolean value = pTrueProps.contains(proposition);
            map.put(proposition.getName(), getValue(value));
        }

        try {
            return evaluate(map);
        } catch (EvaluationException ex) {
            throw new InvalidExpressionException(expression);
        }
    }

    private boolean evaluate(final Map<String, String> pMap) throws EvaluationException {
        //code vars
        String jEvalExp = codeJEvalVariables(pMap);
        //replace operators tokens
        String codedJEvalExp = replaceJEvalOperators(jEvalExp);
        //create evaluator
        Evaluator eval = createEvaluator(pMap);
        //evaluate
        return eval.getBooleanResult(codedJEvalExp);
    }

    @Override
    public String toString() {
        return expression;
    }

    public void add(final Expression pExpression, final BinaryOperator pOperator) {
        if (expression == null) {
            expression = pExpression.toString();
        } else if (expression.isEmpty()) {
            expression = pExpression.expression;
        } else {
            String newExp = " " + pOperator + " ";

            if (isPrimitive() || isParenthesized(pExpression) || pExpression.isPrimitive()) {
                newExp += pExpression;
            } else {
                newExp += parenthesize(pExpression);
            }
            //TODO: do not add the same expression (term) 
            //at the end of the current expression

            if (!expression.endsWith(newExp)) {
                expression += newExp;
            }
        }
        optimize();
    }

    public String parenthesize(final Expression pExp) {
        return parenthesize(pExp.toString());
    }

    public String parenthesize() {
        return parenthesize(expression);
    }

    public String parenthesize(String pExt) {
        return "(" + pExt + ")";
    }

    public boolean isParenthesized() {
        return isParenthesized(expression);
    }

    public static boolean isParenthesized(final Expression exp) {
        return isParenthesized(exp.toString());
    }

    public static boolean isParenthesized(final String pExp) {
        final String exp = pExp.trim();
        if (isNegated(exp)) {
            return isParenthesized(exp.substring(1));
        }
        return exp.startsWith("(") && exp.endsWith(")");
    }

    public boolean isPrimitive() {
        return isPrimitive(expression);
    }

    @Override
    //TODO: improve this
    public boolean equals(final Object pObj) {
        if (pObj instanceof Expression) {
            final Expression exp = (Expression) pObj;
            return exp.toString().equals(toString());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (expression != null ? expression.hashCode() : 0);
        return hash;
    }

    private static Evaluator createEvaluator(final Map<String, String> pMap) {
        final Evaluator ev = new Evaluator('\'', false, false, false, false);
        ev.setVariables(pMap);
        return ev;
    }

    //TODO: optimize
    private void optimize() {
        if (!expression.trim().isEmpty() && !isPrimitive()) {
        }
    }

    public Set<Proposition> getPropositions() {
        final Set<Proposition> props = new HashSet<Proposition>();
        for (String pname : getPropositionsNames()) {
            props.add(new Proposition(pname));
        }
        return props;
    }

    public Collection<String> getPropositionsNames() {
        final Collection<String> propositions = new HashSet<String>();
        final StringTokenizer st = new StringTokenizer(expression, VARIABLES_DELIMS);

        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            propositions.add(token);
        }
        return propositions;
    }

    private String getValue(final boolean pValue) {
        return pValue ? JEVAL_TRUE_VALUE : JEVAL_FALSE_VALUE;
    }

    private Collection<String> getTerms() {
        final Collection<String> terms = new HashSet<String>();
        final StringTokenizer st = new StringTokenizer(expression.replaceAll(" ", ""), EXPS_DELIMS, false);
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            terms.add(token);
        }

        return terms;
    }

    //TODO: POG: test
    public boolean contains(final Expression pExpression) {
        for (final String term : getTerms()) {
            if (!isParenthesized(term) && term.equals(pExpression.toString())) {
                return true;
            }
        }

        return false;
    }

    private String codeJEvalVariables(Map<String, String> pMap) {
        final StringBuilder sb = new StringBuilder();
        final StringTokenizer st = new StringTokenizer(expression, EVALUATE_DELIMS, true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (pMap.containsKey(token)) {
                sb.append("#{").append(token).append("}");
            } else {
                sb.append(token);
            }
        }

        return sb.toString();
    }

    private String replaceJEvalOperators(String pJEvalExp) {
        String codedJEvalExp = pJEvalExp.replace(AND.getToken(), JEVAL_AND_TOKEN);
        codedJEvalExp = codedJEvalExp.replace(OR.getToken(), JEVAL_OR_TOKEN);

        return codedJEvalExp;
    }
}
