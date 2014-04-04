package org.emast.model.algorithm.iteration.rl.erg;

import java.util.List;

import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;

public interface MultiERGLearning {

	public abstract List<ReinforcementLearning<ERG>> getLearnings();

}