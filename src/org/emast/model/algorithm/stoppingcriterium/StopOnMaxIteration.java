package org.emast.model.algorithm.stoppingcriterium;

import static org.emast.util.DefaultTestProperties.*;

import org.emast.model.algorithm.iteration.IterationValues;

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
