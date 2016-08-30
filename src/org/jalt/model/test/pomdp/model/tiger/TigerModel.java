package org.jalt.model.test.pomdp.model.tiger;

import java.util.Arrays;
import java.util.HashSet;

import org.jalt.model.action.Action;
import org.jalt.model.function.ObservationFunction;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.impl.POMDPModel;
import org.jalt.model.observation.Observation;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso Vicoso
 */
public class TigerModel extends POMDPModel {

    public TigerModel() {
        //create the system states
        final State tigerLeft = new State("TigerLeft");
        final State tigerRight = new State("TigerRight");
        setStates(Arrays.asList(tigerLeft, tigerRight));
        //create the actions
        final Action openLeft = new Action("openLeft");
        final Action openRight = new Action("openRight");
        final Action listen = new Action("listen");
        setActions(Arrays.asList(openLeft, openRight, listen));
        //create the observations
        final Observation listenTigerLeft = new Observation("listenTigerLeft");
        final Observation listenTigerRight = new Observation("listenTigerRight");
        setObservations(new HashSet<Observation>(Arrays.asList(listenTigerLeft, listenTigerRight)));
        //create the transition function
        final TransitionFunction tFunction = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                if (pAction.equals(listen)) {
                    return pState.equals(pFinalState) ? 1.0 : 0.0;
                }
                return 0.5;
            }
        };
        setTransitionFunction(tFunction);
        //create the reward function
        final RewardFunction rFunction = new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                if (pState.equals(tigerLeft) && pAction.equals(openLeft)) {
                    return -100;
                } else if (pState.equals(tigerRight) && pAction.equals(openLeft)) {
                    return 30;
                } else if (pState.equals(tigerLeft) && pAction.equals(openRight)) {
                    return 30;
                } else if (pState.equals(tigerRight) && pAction.equals(openRight)) {
                    return -100;
                } else if (pAction.equals(listen)) {
                    return -2;
                }

                return 0.0;
            }
        };
        setRewardFunction(rFunction);
        //create observation function
        final ObservationFunction oFunction = new ObservationFunction() {
            @Override
            public double getValue(State pState, Observation pObservation, Action pAction) {
                if (pState.equals(tigerLeft) && pObservation.equals(listenTigerLeft)
                        && pAction.equals(listen)) {
                    return 0.85;
                } else if (pState.equals(tigerLeft) && pObservation.equals(listenTigerRight)
                        && pAction.equals(listen)) {
                    return 0.15;
                } else if (pState.equals(tigerRight) && pObservation.equals(listenTigerLeft)
                        && pAction.equals(listen)) {
                    return 0.15;
                } else if (pState.equals(tigerRight) && pObservation.equals(listenTigerRight)
                        && pAction.equals(listen)) {
                    return 0.85;
                } else if (pObservation.equals(listenTigerRight)) {
                    return 0.5;
                } else if (pObservation.equals(listenTigerLeft)) {
                    return 0.5;
                }

                return 0.0;
            }
        };
        setObservationFunction(oFunction);
    }
}
