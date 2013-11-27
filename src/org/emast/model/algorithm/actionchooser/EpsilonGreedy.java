package org.emast.model.algorithm.actionchooser;

import static org.emast.util.DefaultTestProperties.EPSILON;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * The agent chooses the action with the highest Q-value epsilon factor times
 * 
 * @author Anderson
 * 
 */
public class EpsilonGreedy<T> implements ValuedObjectChooser<T> {

	private double epsilon = EPSILON;
	private Random rand = new Random();

	public EpsilonGreedy() {
	}

	public EpsilonGreedy(double epsilon) {
		this.epsilon = epsilon;
	}

	@Override
	public T choose(Map<T, Double> pValues, State state) {
		T action;
		double rnd = rand.nextDouble();
		if (rnd < epsilon) {
			// select random
			action = CollectionsUtils.draw(pValues);
		} else {
			// select max action
			double max = Collections.max(pValues.values());
			Set<T> maxActions = CollectionsUtils.getKeysForValue(pValues, max);
			action = CollectionsUtils.getRandom(maxActions);
		}

		return action;
	}
}
