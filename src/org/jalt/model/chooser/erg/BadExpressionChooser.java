package org.jalt.model.chooser.erg;

import java.util.Map;
import java.util.Set;

import org.jalt.model.chooser.Chooser;
import org.jalt.model.chooser.ThresholdChooser;
import org.jalt.model.propositional.Expression;

/**
 * 
 * @author andvicoso
 */
public class BadExpressionChooser implements Chooser<Expression> {

	private final Chooser<Expression> threshold;
	private final Set<Expression> avoid;

	public BadExpressionChooser(double threshold, Set<Expression> pAvoid) {
		this.threshold = new ThresholdChooser<Expression>(threshold, true);
		this.avoid = pAvoid;
	}

	@Override
	public Set<Expression> choose(Map<Expression, Double> pValues) {
		Set<Expression> chosen = threshold.choose(pValues);
		chosen.removeAll(avoid);

		return chosen;
	}

	@Override
	public Expression chooseOne(Map<Expression, Double> pValues) {
		Set<Expression> chosen = choose(pValues);
		return chosen.isEmpty() ? null : chosen.iterator().next();
	}
}
