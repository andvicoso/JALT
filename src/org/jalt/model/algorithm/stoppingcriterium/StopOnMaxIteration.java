package org.jalt.model.algorithm.stoppingcriterium;

import static org.jalt.util.DefaultTestProperties.*;

import org.jalt.model.algorithm.rl.dp.IterationValues;

/**
 * 
 * @author andvicoso
 */
public class StopOnMaxIteration implements StoppingCriterium {

	private int max;

	public StopOnMaxIteration() {
		this(MAX_ITERATIONS);
	}

	public StopOnMaxIteration(int max) {
		this.max = max;
	}

	@Override
	public boolean isStop(IterationValues values) {
		return values.getIterations() == max;
	}
}
