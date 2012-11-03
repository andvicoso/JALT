package org.emast.model.model.impl;

import java.util.Collection;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.function.ObservationFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.PO;
import org.emast.model.observation.Observation;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class POMDPModel extends MDPModel implements PO {

    private ObservationFunction observationFunction;
    private Set<Observation> observations;

    public POMDPModel() {
    }

    public POMDPModel(ObservationFunction observationFunction, Set<Observation> observations,
            TransitionFunction transitionFunction, RewardFunction rewardFunction,
            Collection<State> states, Collection<Action> actions, int agents) {
        super(transitionFunction, rewardFunction, states, actions, agents);
        this.observationFunction = observationFunction;
        this.observations = observations;
    }

    @Override
    public Set<Observation> getObservations() {
        return observations;
    }

    @Override
    public void setObservations(Set<Observation> observations) {
        this.observations = observations;
    }

    @Override
    public ObservationFunction getObservationFunction() {
        return observationFunction;
    }

    @Override
    public void setObservationFunction(ObservationFunction observationFunction) {
        this.observationFunction = observationFunction;
    }
}
