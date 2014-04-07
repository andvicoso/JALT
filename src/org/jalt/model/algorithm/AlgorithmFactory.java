package org.jalt.model.algorithm;

import org.jalt.model.model.MDP;

/**
 * 
 * @author andvicoso
 */
public interface AlgorithmFactory<M extends MDP, R> {

	Algorithm<M, R> create();

}
