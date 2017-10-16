package org.jalt.model.algorithm.rl.dp;

import java.util.Map;

import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public interface IterationValues {

    Map<State, Double> getCurrentValues();

    Map<State, Double> getLastValues();

    int getIterations();
}
