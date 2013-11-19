package org.emast.model.algorithm.stoppingcriterium;

import java.util.Collections;
import java.util.Set;

import org.emast.model.algorithm.iteration.IterationValues;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.BadExpressionChooser;
import org.emast.model.chooser.Chooser;
import org.emast.model.propositional.Expression;

/**
 * 
 * @author anderson
 */
public class StopOnBadExpression implements StoppingCriterium {

	private Chooser<Expression> chooser;

	public StopOnBadExpression(double threshold, Set<Expression> pAvoid) {
		this.chooser = new BadExpressionChooser(threshold, pAvoid);
	}

	@Override
	public boolean isStop(IterationValues pValues) {
		Set<Expression> chosen = Collections.emptySet();
		if (pValues instanceof ReinforcementLearning) {
			ReinforcementLearning<?> alg = (ReinforcementLearning<?>) pValues;
			if (alg.getQTable() instanceof ERGQTable) {
				ERGQTable q = (ERGQTable) alg.getQTable();
				chosen = chooser.choose(q.getExpsValues());
			}
		}

		return !chosen.isEmpty();
	}
}
