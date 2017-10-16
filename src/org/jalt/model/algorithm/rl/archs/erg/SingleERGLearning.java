package org.jalt.model.algorithm.rl.archs.erg;

import org.jalt.model.algorithm.rl.ReinforcementLearning;
import org.jalt.model.model.ERG;

public interface SingleERGLearning {

	public abstract ReinforcementLearning<ERG> getLearning();

}