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
import org.emast.model.function.transition.BlockedGridTransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.impl.GridModel;
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
		// POG
		if (model instanceof GridModel) {
			GridModel gridModel = (GridModel) model;
			int rows = gridModel.getRows();
			int cols = gridModel.getCols();
			model.setTransitionFunction(new BlockedGridTransitionFunction(rows, cols, blocked));
		}
		Expression badExp;
		Policy policy;
		pParameters.put(QTable.NAME, q);
		// initialize
		avoid.clear();
		blocked.clear();
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
				populateBlocked(model, badExp);
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

	protected void populateBlocked(ERG model, Expression toBlock) {
		// mark as blocked all states that contains one of the "avoid" expressions
		for (State state : model.getStates()) {
			Expression exp = model.getPropositionFunction().getExpressionForState(state);
			if (toBlock.equals(exp)) {
				Map<State, Action> sources = model.getTransitionFunction().getSources(
						model.getStates(), model.getActions(), state);
				if (!blocked.containsKey(state))
					blocked.put(state, new HashSet<Action>());
				blocked.get(state).add(sources.get(state));
				// Log.info("Blocked state:" + state + " and action: " + action);
			}
		}
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}
}
