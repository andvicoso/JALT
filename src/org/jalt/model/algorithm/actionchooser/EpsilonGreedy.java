package org.jalt.model.algorithm.actionchooser;

import static org.jalt.util.DefaultTestProperties.EPSILON;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 * The agent chooses the action with the highest Q-value epsilon factor times
 * 
 * @author andvicoso
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
		T action = null;
		double rnd = rand.nextDouble();
		if (rnd < epsilon) {
			// select random
			action = CollectionsUtils.draw(pValues);
		} else if (!pValues.isEmpty()){
			// select max action
			double max = Collections.max(pValues.values());
			Set<T> maxActions = CollectionsUtils.getKeysForValue(pValues, max);
			action = CollectionsUtils.getRandom(maxActions);
		}

		return action;
	}
}
