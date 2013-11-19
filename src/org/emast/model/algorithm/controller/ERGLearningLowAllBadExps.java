package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.util.ERGLearningUtils;

/**
 * QLearning + PPFERG + baixando o valor de todas as expressões de uma vez
 */
public class ERGLearningLowAllBadExps extends AbstractERGLearningLowExp {

	public ERGLearningLowAllBadExps(ReinforcementLearning<ERG> learning) {
		super(learning);
	}

	@Override
	public Policy run(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		ThresholdChooser<Expression> badChooser = new ThresholdChooser<Expression>(BAD_EXP_VALUE,
				true);
		Set<Expression> avoid = new HashSet<Expression>();
		Problem<ERG> p = pProblem;
		ERG model = p.getModel();

		ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
		pParameters.put(QTable.NAME, q);
		// 1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING)
		learning.run(p, pParameters);
		// 2. GET BAD EXPRESSIONS FROM QLEARNING ITERATIONS
		Set<Expression> badExps = badChooser.choose(q.getExpsValues());
		// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
		// WHICH HAVE THE FOUND EXPRESSIONS
		if (!badExps.isEmpty()) {
			avoid.addAll(badExps);
			Log.info("\nAvoid: " + avoid);

			updateQTable(model, q, avoid);
			Log.info("\nQTable: \n" + q.toString());
		}
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		model = ERGLearningUtils.createModel(model, q, avoid);
		p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
		// 5. RUN PPFERG FOR THE NEW MODEL
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
		return ppferg.run(p, pParameters);
	}
}
