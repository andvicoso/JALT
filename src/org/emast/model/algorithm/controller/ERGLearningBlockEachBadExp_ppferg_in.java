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
@Deprecated
public class ERGLearningBlockEachBadExp_ppferg_in extends AbstractERGLearningBlockExp {

	public ERGLearningBlockEachBadExp_ppferg_in(ReinforcementLearning<ERG> learning) {
		super(learning);
	}

	@Override
	public Policy run(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		int iteration = 0;
		Problem<ERG> p = pProblem;
		ERG model = p.getModel();
		ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
		Expression badExp;
		Policy policy;
		// start main loop
		initilize(model);
		do {
			iteration++;
			// Log.info("\nITERATION " + iteration + ":");
			pParameters.put(QTable.NAME, q);
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
			policy = learning.run(p, pParameters);
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			badExp = expFinder.chooseOne(q.getExpsValues());
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
			// WHICH HAVE THE FOUND EXPRESSION
			if (isValid(badExp)) {
				Log.info("Found bad expression: " + badExp);
				avoid.add(badExp);
				// Log.info("Avoid: " + avoid);
				// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
				model = ERGLearningUtils.createModel(model, q, avoid);
				// create problem
				p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
				// 5. CREATE PPFERG ALGORITHM
				final PPFERG<ERG> ppferg = new PPFERG<ERG>();
				// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW MODEL
				policy = ppferg.run(p, pParameters);
			}
		} while (isValid(badExp));

		return policy;
	}
}
