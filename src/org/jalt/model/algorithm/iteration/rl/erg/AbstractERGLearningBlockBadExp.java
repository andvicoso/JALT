package org.jalt.model.algorithm.iteration.rl.erg;

import static org.jalt.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.erg.ERGLearningUtils;

/**
 * 
 * @author andvicoso
 */
public abstract class AbstractERGLearningBlockBadExp implements Algorithm<ERG, Policy> {
	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Set<State> blocked = new HashSet<>();
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

	protected void populateBlocked(ERG model, Expression toBlock) {
		// mark as blocked all states that contains one of the "avoid"
		// expressions
		PropositionFunction pf = model.getPropositionFunction();
		for (State state : model.getStates()) {
			Expression exp = pf.getExpressionForState(state);
			// toBlock.evaluate(pf.getPropositionsForState(state))
			if (toBlock.equals(exp)) {
				blocked.add(state);
			}
		}
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

	@Override
	public Policy run(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		int iteration = 0;
		Problem<ERG> prob = pProblem;
		ERG model = prob.getModel();
		ERGQTable q;
		Expression badExp;
		Policy policy;
		// initialize
		initilize(model);
		// start main loop
		do {
			iteration++;
			q = new ERGQTable(model.getStates(), model.getActions());
			pParameters.put(QTable.NAME, q);
			// Log.info("\nITERATION " + iteration + ":");
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
			policy = runLearning(prob, pParameters);
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

		if (!avoid.isEmpty()) {
			policy = extractPolicyPPFERG(pParameters, prob, model, q);
		} else {
			policy = q.getPolicy();
		}

		// Log.info("Preservation goal:" + model.getPreservationGoal());
		// Log.info("\nQTable: \n" + q.toString(model));

		return policy;
	}

	protected Policy extractPolicyPPFERG(Map<String, Object> pParameters, Problem<ERG> prob,
			ERG model, ERGQTable q) {
		Policy policy;
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		model = ERGLearningUtils.createModel(model, q, avoid);
		// create problem
		prob = new Problem<ERG>(model, prob.getInitialStates(), prob.getFinalStates());
		// q learning policy
		// Log.info(prob.toString(policy));
		// q learning policy - best (greater q values) actions
		// Log.info(prob.toString(policy.optimize()));
		// 5. CREATE PPFERG ALGORITHM
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW
		// MODEL
		policy = ppferg.run(prob, pParameters);
		// after ppferg
		// Log.info(prob.toString(policy));
		// 7. GET THE FINAL POLICY FROM THE PPFERG VIABLE POLICIES
		policy = new Policy(ERGLearningUtils.optmize(policy, q));
		// after optimize
		// Log.info(prob.toString(policy));
		return policy;
	}

	protected abstract Policy runLearning(Problem<ERG> prob, Map<String, Object> pParameters);

}
