package org.emast.util.grid.distancemeasure;

import org.emast.model.state.State;

/**
 *
 * @author andvicoso
 */
public interface DistanceMeasure {

    double getDistance(final State pS1, final State pS2);
}
