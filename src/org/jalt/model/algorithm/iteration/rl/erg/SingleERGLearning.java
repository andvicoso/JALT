package org.jalt.model.algorithm.iteration.rl.erg;

import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.model.ERG;

public interface SingleERGLearning {

	public abstract ReinforcementLearning<ERG> getLearning();

}