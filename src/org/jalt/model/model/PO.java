package org.jalt.model.model;

import java.util.Set;

import org.jalt.model.function.ObservationFunction;
import org.jalt.model.observation.Observation;

/**
 *
 * @author andvicoso
 */
public interface PO extends MDP {

    Set<Observation> getObservations();

    ObservationFunction getObservationFunction();

    void setObservations(Set<Observation> obs);

    void setObservationFunction(ObservationFunction of);
}
