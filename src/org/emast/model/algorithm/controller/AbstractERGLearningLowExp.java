package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_Q_PERCENT;

import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public abstract class AbstractERGLearningLowExp extends AbstractERGLearning {

	public AbstractERGLearningLowExp(ReinforcementLearning<ERG> learning) {
		super(learning);
	}

	protected ERGQTable updateQTable(ERG model, ERGQTable q, Set<Expression> avoid) {
		for (State state : model.getStates()) {
			for (Action action : model.getActions()) {
				ERGQTableItem item = q.get(state, action);
				Expression exp = item.getExpression();
				double value = item.getValue();
				if (exp != null && !exp.getPropositions().isEmpty() && matchExpression(exp, avoid)) {
					value += value * BAD_Q_PERCENT;// BAD_Q_VALUE;
				}

				q.put(state, action, newItem(item, value));
			}
		}

		return q;
	}

	private ERGQTableItem newItem(ERGQTableItem item, double value) {
		ERGQTableItem nitem = new ERGQTableItem(item);
		nitem.setValue(value);
		return nitem;
	}

	private boolean matchExpression(Expression stateExp, Set<Expression> exps) {
		for (Expression exp : exps) {
			if (exp.equals(stateExp)) {
				return true;
			}
		}

		return false;
	}

}
