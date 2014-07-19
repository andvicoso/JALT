package org.jalt.model.algorithm.iteration.rl.erg;

import static org.jalt.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.reachability.PPFERG;
import org.jalt.model.algorithm.stoppingcriterium.StopOnBadExpression;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.jalt.model.algorithm.table.QTable;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.chooser.BadExpressionChooser;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.transition.BlockedGridTransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.model.test.MainTest;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.erg.ERGFactory;
import org.jalt.util.erg.ERGLearningUtils;

/**
 * 
 * @author andvicoso
 */
public abstract class AbstractERGLearningBlockBadExp implements Algorithm<ERG, Policy> {
	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Set<State> blocked = new TreeSet<>();
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	protected void initilize(ERG model) {
		avoid.clear();
		blocked.clear();
		// POG
		if (model instanceof GridModel) {
			GridModel gridModel = (GridModel) model;
			int rows = gridModel.getRows();
			int cols = gridModel.getCols();
			model.setTransitionFunction(new BlockedGridTransitionFunction(rows, cols, blocked));
		}// else//TODO:
	}

	@Override
	public Policy run(Problem<ERG> prob, Map<String, Object> params) {
		int iteration = 0;
		ERG model = prob.getModel();
		ERGQTable q;
		Expression badExp;
		Policy policy;
		// initialize
		initilize(model);
		boolean valid = false;
		// start main loop
		do {
			iteration++;
			// CREATE AND SAVE Q-TABLE (SHARED BY ALL AGENTS, IF MORE THAN ONE)
			q = new ERGQTable(model.getStates(), model.getActions());
			params.put(QTable.NAME, q);
			// Log.info("\nITERATION " + iteration + ":");
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
			policy = runLearning(prob, params);
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			badExp = expFinder.chooseOne(q.getExpsValues());
			// valid expression: not null and not empty
			valid = isValid(badExp);
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN
			// QLEARNING EXPLORATION AND HAVE THE FOUND EXPRESSION
			if (valid) {
				Log.info("Found bad expression: " + badExp);
				// avoid bad exp
				avoid.add(badExp);
				// Log.info("Avoid: " + avoid);
				populateBlocked(q);
				// run vi again->new environment to compare
				MainTest.runVI(prob, params);
			}
		} while (valid);

		if (!avoid.isEmpty()) {
			policy = extractPolicyPPFERG(params, prob, model, q);
		} else {
			policy = q.getPolicy();
		}

		// Log.info("Preservation goal:" + model.getPreservationGoal());
		// Log.info("\nQTable: \n" + q.toString(model));

		return policy;
	}

	protected void populateBlocked(ERGQTable q) {
		// mark as blocked all states that 
		//contains one of the "avoid" expressions
		PropositionFunction pf = ERGFactory.createPropositionFunction(q);
		for (State state : q.getStates()) {
			Expression exp = pf.getExpressionForState(state);
			for (Expression toBlock : avoid) {
				if (toBlock.equals(exp)) {
					blocked.add(state);
				}
			}
		}
		
		 Log.info("blocked: " + blocked);
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nBad exp reward param: ").append(BAD_EXP_VALUE);

		return sb.toString();
	}

	protected StoppingCriteria getStopCriteria() {
		return new StoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE, avoid),
				DefaultTestProperties.DEFAULT_STOPON);
	}

	protected Policy extractPolicyPPFERG(Map<String, Object> pParameters, Problem<ERG> pProb,
			ERG oldModel, ERGQTable q) {
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		ERG newModel = ERGLearningUtils.createModel(oldModel, q, avoid);
		Problem<ERG> newProblem = new Problem<>(newModel, pProb.getInitialStates(),
				pProb.getFinalStates());
		// 5. CREATE PPFERG ALGORITHM
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
		// Log.info("Starting PPFERG...");
		Policy policy = ppferg.run(newProblem, pParameters);
		// after ppferg
		// Log.info(prob.toString(policy));
		return policy;
	}

	protected abstract Policy runLearning(Problem<ERG> prob, Map<String, Object> pParameters);

}
