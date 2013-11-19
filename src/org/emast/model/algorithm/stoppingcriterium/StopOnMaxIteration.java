package org.emast.model.algorithm.stoppingcriterium;

import static org.emast.util.DefaultTestProperties.*;

import org.emast.model.algorithm.iteration.IterationValues;

/**
 * 
 * @author Anderson
 */
public class StopOnMaxIteration implements StoppingCriterium {

	@Override
	public boolean isStop(IterationValues values) {
		return values.getIterations() == MAX_ITERATIONS;
	}
}
