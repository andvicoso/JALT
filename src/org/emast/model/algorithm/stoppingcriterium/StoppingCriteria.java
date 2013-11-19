package org.emast.model.algorithm.stoppingcriterium;

import org.emast.model.algorithm.iteration.IterationValues;

public class StoppingCriteria implements StoppingCriterium {

	private StoppingCriterium[] criteria;

	public StoppingCriteria(StoppingCriterium... pCriteria) {
		criteria = pCriteria;
	}

	@Override
	public boolean isStop(IterationValues iterationValues) {
		for (StoppingCriterium sc : criteria) {
			if (sc.isStop(iterationValues))
				return true;
		}
		return false;
	}
}
