package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.stoppingcriterium.StopOnBadExpression;
import org.emast.model.algorithm.stoppingcriterium.StopOnError;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.BadExpressionChooser;
import org.emast.model.chooser.Chooser;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.ERGLearningUtils;

/**
 * Learning + PPFERG + bloqueando a pior expressão de cada vez (com iteração)
 */
public class MultiERGLearningBlockEachBadExp extends MultiAgentERGLearning {

	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Map<State, Set<Action>> blocked = new HashMap<State, Set<Action>>();
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	public MultiERGLearningBlockEachBadExp(List<ReinforcementLearning<ERG>> learnings) {
		super(learnings);
		StoppingCriteria stop = new StoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE, avoid),
				new StopOnError());
		for (ReinforcementLearning<ERG> learning : learnings) {
			learning.setStoppingCriterium(stop);
		}
	}

	@Override
	public Policy run(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		int iteration = 0;
		Problem<ERG> prob = pProblem;
		ERG model = prob.getModel();
		ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
		Expression badExp;
		Policy policy;
		pParameters.put(QTable.NAME, q);

		initilize(model);
		// start main loop
		do {
			iteration++;
			// Log.info("\nITERATION " + iteration + ":");
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
			runAll(prob, pParameters);
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			badExp = expFinder.chooseOne(q.getExpsValues());
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN
			// QLEARNING EXPLORATION AND HAVE THE FOUND EXPRESSION
			if (isValid(badExp)) {
				Log.info("Found bad expression: " + badExp);
				// avoid bad exp
				avoid.add(badExp);
				// Log.info("Avoid: " + avoid);
				populateBlocked(q);
			}
		} while (isValid(badExp));

		policy = q.getPolicy();

		if (!avoid.isEmpty()) {
			// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
			model = ERGLearningUtils.createModel(model, q, avoid);
			// create problem
			prob = new Problem<ERG>(model, prob.getInitialStates(), prob.getFinalStates());
			// learning policy
			// Log.info(prob.toString(policy));
			// learning policy - best (greater q values) actions
			// Log.info(prob.toString(policy.optimize()));
			// 5. CREATE PPFERG ALGORITHM
			final PPFERG<ERG> ppferg = new PPFERG<ERG>();
			// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW MODEL
			policy = ppferg.run(prob, pParameters);
			// after ppferg
			// Log.info(prob.toString(policy));
			// 7. GET THE FINAL POLICY FROM THE PPFERG VIABLE POLICIES
			policy = new Policy(ERGLearningUtils.optmize(policy, q));
			// after optimize
			// Log.info(prob.toString(policy));
		}

		Log.info("Preservation goal:" + model.getPreservationGoal());

		return policy;
	}

	private void runAll(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		int count = 0;
		List<Policy> policies = new ArrayList<>();
		for (ReinforcementLearning<ERG> learning : learnings) {
			runThread(pProblem, pParameters, learning, policies, count++);
		}

		while (policies.size() < learnings.size()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void runThread(final Problem<ERG> pProblem, final Map<String, Object> pParameters,
			final ReinforcementLearning<ERG> learning, final List<Policy> policies, final int i) {

		new Thread() {
			@Override
			public void run() {
				Map<String, Object> map = new HashMap<>(pParameters);
				map.put(ReinforcementLearning.AGENT_NAME, i);
				policies.add(learning.run(pProblem, map));
			}
		}.start();
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

	protected void initilize(ERG model) {
		avoid.clear();
		blocked.clear();

		final TransitionFunction oldtf = model.getTransitionFunction();
		TransitionFunction tf = new TransitionFunction() {

			@Override
			public double getValue(State pState, State pFinalState, Action pAction) {
				boolean isBlocked = blocked.containsKey(pState)
						&& blocked.get(pState).contains(pAction);
				return isBlocked ? 0.0 : oldtf.getValue(pState, pFinalState, pAction);
			}
		};

		model.setTransitionFunction(tf);
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}
}
