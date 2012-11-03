package org.emast.model.model;

import java.util.Set;
import org.emast.model.function.ObservationFunction;
import org.emast.model.observation.Observation;

/**
 *
 * @author Anderson
 */
public interface PO extends MDP {

    Set<Observation> getObservations();

    ObservationFunction getObservationFunction();

    void setObservations(Set<Observation> obs);

    void setObservationFunction(ObservationFunction of);
}
