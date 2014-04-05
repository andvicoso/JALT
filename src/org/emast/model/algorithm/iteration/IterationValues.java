package org.emast.model.algorithm.iteration;

import java.util.Map;

import org.emast.model.state.State;

/**
 *
 * @author andvicoso
 */
public interface IterationValues {

    Map<State, Double> getCurrentValues();

    Map<State, Double> getLastValues();

    int getIterations();
}
