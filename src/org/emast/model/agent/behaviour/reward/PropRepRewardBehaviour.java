package org.emast.model.agent.behaviour.reward;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.agent.Agent;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class PropRepRewardBehaviour<M extends ERG> implements PropRewardBehaviour<M> {

    protected double badRewardThreshold;
    protected Map<Proposition, Double> localPropositionsReputation;

    public PropRepRewardBehaviour(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
        localPropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
    public void manageReward(Agent pAgent, M pModel, State pNextState, double pReward) {
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pModel, pNextState, pReward, localPropositionsReputation);
    }

    private void savePropositionReputation(M pModel, State pNextState,
            double pReward, Map<Proposition, Double> pPropositionsReputation) {
        if (pPropositionsReputation != null) {
            //bad reward value is distributed equally over the state`s propostions
            Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pNextState);
            if (props != null) {
                double propReward = pReward / props.size();
                for (Proposition proposition : props) {
                    Double currPropReward = pPropositionsReputation.get(proposition);
                    currPropReward = currPropReward == null ? 0d : currPropReward;
                    pPropositionsReputation.put(proposition, propReward + currPropReward);
                }
            }
        }
    }

    @Override
    public Map<Proposition, Double> getResult() {
        return localPropositionsReputation;
    }
}
