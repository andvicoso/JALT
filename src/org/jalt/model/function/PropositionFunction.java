package org.jalt.model.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jeval.EvaluationException;

import org.jalt.model.exception.InvalidExpressionException;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.propositional.operator.BinaryOperator;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridUtils;

/**
 * 
 * @author And
 */
public class PropositionFunction implements Serializable {

	private Map<State, Set<Proposition>> table;

	public PropositionFunction() {
		table = new HashMap<State, Set<Proposition>>();
	}

	public void add(final State pState, final Collection<Proposition> pProps) {
		Set<Proposition> props = getProps(pState);

		props.addAll(pProps);
	}

	public void set(final State pState, final Collection<Proposition> pProps) {
		removeAll(pState);
		add(pState, pProps);
	}

	public void add(final State pState, final Proposition... pProps) {
		Set<Proposition> props = getProps(pState);

		props.addAll(Arrays.asList(pProps));
	}

	public void remove(final State pState, final Proposition... pProps) {
		Set<Proposition> props = table.get(pState);

		if (props != null) {
			props.removeAll(Arrays.asList(pProps));
		}
	}

	public void removeAll(final State pState) {
		table.put(pState, null);
	}

	/**
	 * return states that satisfies the expression
	 * 
	 * @param pExpression
	 * @param pPropositions
	 * @return
	 * @throws EvaluationException
	 */
	public Set<State> intension(final Collection<State> pModelStates,
			final Set<Proposition> pModelProps, final Expression pExpression)
			throws InvalidExpressionException {
		final Set<State> result = new HashSet<State>();

		for (final State state : pModelStates) {
			if (satisfies(state, pExpression)) {
				result.add(state);
			}
		}

		return result;
	}

	public boolean satisfies(final State pState, final Expression pExpression)
			throws InvalidExpressionException {
		Set<Proposition> props = table.get(pState);
		if (props == null) {
			props = Collections.emptySet();
		}
		return pExpression.isEmpty() || pExpression.evaluate(props);
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
		return pState != null && pItemState != null && pState.equals(pItemState);
	}

	public void addGridStatePropositions(int pRow, int pCol, String... pPropsNames) {
		final List<Proposition> props = new ArrayList<Proposition>(pPropsNames.length);

		for (final String propName : pPropsNames) {
			props.add(new Proposition(propName));
		}

		add(GridUtils.STATES_CACHE[pRow][pCol], props.toArray(new Proposition[props.size()]));
	}

	public Expression getExpressionForState(State pState) {
		final Set<Proposition> props = getPropositionsForState(pState);
		return props != null ? new Expression(BinaryOperator.AND, props) : new Expression("");
	}

	public void addGridStatePropositions(int pRow, int pCol, Proposition... pProps) {
		add(GridUtils.STATES_CACHE[pRow][pCol], pProps);
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

	private Set<Proposition> getProps(final State pState) {
		Set<Proposition> props = table.get(pState);
		if (props == null) {
			props = new HashSet<Proposition>();
			table.put(pState, props);
		}
		return props;
	}

	public void add(Set<State> finalStates, Proposition finalProp) {
		for (State state : finalStates) {
			add(state, finalProp);
		}
	}

}
