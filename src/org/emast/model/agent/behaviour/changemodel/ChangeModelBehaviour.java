package org.emast.model.agent.behaviour.changemodel;

import org.emast.model.agent.Agent;
import org.emast.model.agent.behaviour.IndividualBehaviour;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public interface ChangeModelBehaviour<M extends MDP> extends IndividualBehaviour {

    void changeModel(Agent pAgent, M pModel, State pState);
}
