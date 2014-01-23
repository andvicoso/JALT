package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.stoppingcriterium.StopOnBadExpression;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.emast.model.chooser.BadExpressionChooser;
import org.emast.model.chooser.Chooser;
import org.emast.model.function.transition.BlockedGridTransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.impl.GridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;
import org.emast.util.DefaultTestProperties;

public abstract class AbstractERGLearningBlockExp extends AbstractERGLearning {

	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Map<State, Set<Action>> blocked = new HashMap<State, Set<Action>>();
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	public AbstractERGLearningBlockExp(ReinforcementLearning<ERG> learning) {
		super(learning);
		// replace the default stopping criterium of the learning algorithm
		learning.setStoppingCriterium(new StoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE,
				avoid), DefaultTestProperties.DEFAULT_STOPON));// 
	}

	protected void initilize(ERG model) {
		avoid.clear();
		blocked.clear();
		// POG
		if (model instanceof GridModel) {
			GridModel gridModel = (GridModel) model;
			int rows = gridModel.getRows();
			int cols = gridModel.getCols();
			model.setTransitionFunction(new BlockedGridTransitionFunction(rows, cols, blocked));
		}
	}

	protected void populateBlocked(ERG model, Expression toBlock) {
		// mark as blocked all states that contains one of the "avoid" expressions
		for (State state : model.getStates()) {
			Expression exp = model.getPropositionFunction().getExpressionForState(state);
			if (toBlock.equals(exp)) {
				// values.remove(state);
				Map<State, Action> sources = model.getTransitionFunction().getSources(
						model.getStates(), model.getActions(), state);
				for (State source : sources.keySet()) {
					if (!blocked.containsKey(source))
						blocked.put(source, new HashSet<Action>());
					blocked.get(source).add(sources.get(source));
					// Log.info("Blocked state:" + state + " and action: " + action);
				}
			}
		}
	}

	// protected void populateBlocked(ERGQTable q) {
	// // mark as blocked all visited states that contains one of the "avoid"
	// // expressions
	// for (Action action : q.getActions()) {
	// for (State state : q.getStates()) {
	// Expression exp = q.get(state, action).getExpression();
	// if (avoid.contains(exp)) {
	// if (!blocked.containsKey(state))
	// blocked.put(state, new HashSet<Action>());
	// if (!blocked.get(state).contains(action))
	// blocked.get(state).add(action);
	// // Log.info("Blocked state:" + state + " and action: " + action);
	// }
	// }
	// }
	// }

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}
}
