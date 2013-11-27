package org.emast.model.algorithm.controller;

import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.util.ERGLearningUtils;

/**
 * Learning + PPFERG + bloqueando a pior expressão de cada vez (com iteração)
 */
public class ERGLearningBlockEachBadExp extends AbstractERGLearningBlockExp {

	public ERGLearningBlockEachBadExp(ReinforcementLearning<ERG> learning) {
		super(learning);
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
			policy = learning.run(prob, pParameters);
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

		if (!avoid.isEmpty()) {
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
			// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW MODEL
			policy = ppferg.run(prob, pParameters);
			// after ppferg
			// /Log.info(prob.toString(policy));
			// 7. GET THE FINAL POLICY FROM THE PPFERG VIABLE POLICIES
			policy = new Policy(ERGLearningUtils.optmize(policy, q));
			// after optimize
			// Log.info(prob.toString(policy));
		} else {
			policy = learning.getQTable().getPolicy();
		}

		 Log.info("Preservation goal:" + model.getPreservationGoal());

		return policy;
	}
}
