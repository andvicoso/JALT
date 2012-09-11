package org.emast.model.function;

import org.emast.model.action.Action;
import org.emast.model.observation.Observation;
import org.emast.model.state.State;

public interface ObservationFunction {

    double getValue(State pState, Observation pObservation, Action pAction);
}
