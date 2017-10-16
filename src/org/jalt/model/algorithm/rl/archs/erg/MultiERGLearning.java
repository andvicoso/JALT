package org.jalt.model.algorithm.rl.archs.erg;

import java.util.List;

import org.jalt.model.algorithm.rl.ReinforcementLearning;
import org.jalt.model.model.ERG;

public interface MultiERGLearning {

	public abstract List<ReinforcementLearning<ERG>> getLearnings();

}