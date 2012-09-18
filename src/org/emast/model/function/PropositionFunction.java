package org.emast.model.function;

import java.util.*;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.state.State;
import org.emast.util.GridUtils;

/**
 *
 * @author And
 */
public class PropositionFunction {

    private Map<State, Set<Proposition>> table;

    public PropositionFunction() {
        table = new HashMap<State, Set<Proposition>>();
    }

    public void add(final State pState, final Proposition... pProps) {
        Set<Proposition> props = table.get(pState);

        if (props == null) {
            props = new HashSet<Proposition>();
            table.put(pState, props);
        }

        props.addAll(Arrays.asList(pProps));
    }

    /**
     * return states that satisfies the expression
     *
     * @param pExpression
     * @param pPropositions
     * @return
     * @throws EvaluationException
     */
    public Collection<State> intension(final Collection<State> pModelStates,
            final Set<Proposition> pModelProps, final Expression pExpression)
            throws InvalidExpressionException {
        final Collection<State> result = new HashSet<State>();

        for (final State state : pModelStates) {
            if (satisfies(state, pExpression)) {
                result.add(state);
            }
        }

        return result;
    }

    public boolean satisfies(final State pState, final Expression pExpression)
            throws InvalidExpressionException {
        for (State state : table.keySet()) {
            if (isStateValid(pState, state)) {
                Set<Proposition> props = table.get(state);

                if (pExpression.evaluate(props)) {
                    return true;
                }
            }
        }

        return pExpression.isEmpty();
    }

    public Set<Proposition> getPropositionsForState(final State pState) {
        return table.get(pState);
    }

    public Set<State> getStatesWithProposition(final Proposition pProp) {
        final Set<State> states = new HashSet<State>();

        for (State state : table.keySet()) {
            Set<Proposition> props = table.get(state);
            if (props.contains(pProp)) {
                states.add(state);
            }
        }

        return states;
    }

    public boolean isStateValid(final State pState, final State pItemState) {
        return pState != null && pItemState != null
                && (pState.equals(pItemState) || pItemState.equals(State.ANY));
    }

    public void addGridStatePropositions(int pRow, int pCol, String... pPropsNames) {
        final List<Proposition> props = new ArrayList<Proposition>(pPropsNames.length);

        for (final String propName : pPropsNames) {
            props.add(new Proposition(propName));
        }

        add(GridUtils.createGridState(pRow, pCol), props.toArray(new Proposition[props.size()]));
    }

    public Expression getExpressionForState(State pState) {
        final Set<Proposition> props = getPropositionsForState(pState);
        return props != null ? new Expression(props, BinaryOperator.AND) : new Expression("");
    }

    public void addGridStatePropositions(int pRow, int pCol, Proposition... pProps) {
        add(GridUtils.createGridState(pRow, pCol), pProps);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<State, Set<Proposition>> entry : table.entrySet()) {
            State state = entry.getKey();
            Set<Proposition> set = entry.getValue();
            sb.append(state).append(set).append(", ");
        }

        return sb.toString();
    }
}
