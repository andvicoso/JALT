package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.stoppingcriterium.StopOnBadExpression;
import org.emast.model.algorithm.stoppingcriterium.StopOnError;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.BadExpressionChooser;
import org.emast.model.chooser.Chooser;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

public abstract class AbstractERGLearningBlockExp extends AbstractERGLearning {

	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Map<State, Set<Action>> blocked = new HashMap<State, Set<Action>>();
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	public AbstractERGLearningBlockExp(ReinforcementLearning<ERG> learning) {
		super(learning);
		learning.setStoppingCriterium(new StoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE,
				avoid), new StopOnError()));
	}

	protected void initilize(ERG model) {
		avoid.clear();
		blocked.clear();
		
		final TransitionFunction oldtf = model.getTransitionFunction();
		TransitionFunction tf = new TransitionFunction() {

			@Override
			public double getValue(State pState, State pFinalState, Action pAction) {
				return blocked.containsKey(pState) && blocked.get(pState).contains(pAction) ? 0.0
						: oldtf.getValue(pState, pFinalState, pAction);
			}
		};

		model.setTransitionFunction(tf);
	}

	protected void populateBlocked(ERGQTable q) {
		// mark as blocked all visited states that contains one of the "avoid"
		// expressions
		for (Action action : q.getActions()) {
			for (State state : q.getStates()) {
				Expression exp = q.get(state, action).getExpression();
				if (avoid.contains(exp)) {
					if (!blocked.containsKey(state))
						blocked.put(state, new HashSet<Action>());
					if (!blocked.get(state).contains(action))
						blocked.get(state).add(action);
					// Log.info("Blocked state:" + state + " and action: " + action);
				}
			}
		}
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}
}
