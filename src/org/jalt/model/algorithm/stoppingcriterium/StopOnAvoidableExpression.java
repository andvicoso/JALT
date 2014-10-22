package org.jalt.model.algorithm.stoppingcriterium;

import java.util.Collections;
import java.util.Set;

import org.jalt.model.algorithm.iteration.IterationValues;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.chooser.BadExpressionChooser;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.propositional.Expression;

/**
 * 
 * @author andvicoso
 */
public class StopOnAvoidableExpression implements StoppingCriterium<ReinforcementLearning> {

	private Chooser<Expression> chooser;

	public StopOnAvoidableExpression(double threshold, Set<Expression> pAvoid) {
		this.chooser = new BadExpressionChooser(threshold, pAvoid);
	}

	@Override
	public boolean isStop(ReinforcementLearning pValues) {
		Set<Expression> chosen = Collections.emptySet();
		if (pValues.getQTable() instanceof ERGQTable) {
			ERGQTable q = (ERGQTable) pValues.getQTable();
			chosen = chooser.choose(q.getExpsValues());
		}

		return !chosen.isEmpty();
	}
}
