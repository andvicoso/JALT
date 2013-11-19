package org.emast.model.algorithm;

import org.emast.model.model.MDP;

public interface AlgorithmFactory<M extends MDP, R> {

	Algorithm<M, R> create();

}
