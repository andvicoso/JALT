package org.jalt.util.grid.distancemeasure;

import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public interface DistanceMeasure {

    double getDistance(final State pS1, final State pS2);
}
