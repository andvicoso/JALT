package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.stoppingcriterium.StopOnBadExpression;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.BadExpressionChooser;
import org.emast.model.chooser.Chooser;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.util.ERGLearningUtils;

/**
 * Learning + baixando o valor de q para a pior expressão de cada vez
 */
public class ERGLearningLowEachBadExp extends AbstractERGLearningLowExp {

	private final Set<Expression> avoid = new HashSet<Expression>();
	private final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	public ERGLearningLowEachBadExp(ReinforcementLearning<ERG> learning) {
		super(learning);
		learning.setStoppingCriterium(new StopOnBadExpression(BAD_EXP_VALUE, avoid));
	}

	@Override
	public Policy run(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		Problem<ERG> p = pProblem;
		ERG model = p.getModel();
		int episodies = 0;
		Expression badExp;
		ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
		// start main loop
		do {
			Log.info("\nEPISODE " + episodies++ + ":\n");
			pParameters.put(QTable.NAME, q);
			// 1. RUN QLEARNING UNTIL A LOW REWARD EXPRESSION IS FOUND (QUICK STOP LEARNING)
			learning.run(p, pParameters);
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			badExp = expFinder.chooseOne(q.getExpsValues());
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
			// WHICH HAVE THE FOUND EXPRESSION
			if (badExp != null) {
				avoid.add(badExp);
				Log.info("Avoid: " + avoid);
				// update q to lower q values for states that contains one badexp
				updateQTable(model, q, avoid);
				// Log.info("\nQTable: \n" + q.toString());
			}
		} while (badExp != null);
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		model = ERGLearningUtils.createModel(model, q, avoid);
		p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
		// 5. RUN PPFERG FOR THE NEW MODEL
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
		return ppferg.run(p, pParameters);
	}
}
