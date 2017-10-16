package org.jalt.model.algorithm.stoppingcriterium;

import java.util.Collections;
import java.util.Set;

import org.jalt.model.algorithm.rl.ReinforcementLearning;
import org.jalt.model.algorithm.rl.dp.IterationValues;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.chooser.erg.BadExpressionChooser;
import org.jalt.model.model.MDP;
import org.jalt.model.propositional.Expression;

/**
 * 
 * @author andvicoso
 */
public class StopOnAvoidableExpression implements StoppingCriterium {

	private Chooser<Expression> chooser;

	public StopOnAvoidableExpression(double threshold, Set<Expression> pAvoid) {
		this.chooser = new BadExpressionChooser(threshold, pAvoid);
	}

	@Override
	public boolean isStop(IterationValues values) {
		Set<Expression> chosen = Collections.emptySet();
		if (values instanceof ReinforcementLearning<?>) {
			ReinforcementLearning<MDP> rlvalues = (ReinforcementLearning<MDP>) values;
			if (rlvalues.getQTable() instanceof ERGQTable) {
				ERGQTable q = (ERGQTable) rlvalues.getQTable();
				chosen = chooser.choose(q.getExpsValues());
			}
		}

		return !chosen.isEmpty();
	}
}
