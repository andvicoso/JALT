package org.jalt.model.function;

import org.jalt.model.action.Action;
import org.jalt.model.observation.Observation;
import org.jalt.model.state.State;

public interface ObservationFunction {

    double getValue(State pState, Observation pObservation, Action pAction);
}
