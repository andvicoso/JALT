package org.emast.model.algorithm.iteration.rl.erg;

import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;

public interface SingleERGLearning {

	public abstract ReinforcementLearning<ERG> getLearning();

}