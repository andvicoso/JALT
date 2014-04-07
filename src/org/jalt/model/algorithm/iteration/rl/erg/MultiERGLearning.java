package org.jalt.model.algorithm.iteration.rl.erg;

import java.util.List;

import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.model.ERG;

public interface MultiERGLearning {

	public abstract List<ReinforcementLearning<ERG>> getLearnings();

}