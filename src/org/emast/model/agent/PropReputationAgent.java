package org.emast.model.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class PropReputationAgent<M extends ERG> extends ERGAgent<M> {

    protected double badRewardThreshold;
    protected Map<Proposition, Double> localPropositionsReputation;

    public PropReputationAgent(int pAgent, double pBadRewardThreshold) {
        super(pAgent);
        badRewardThreshold = pBadRewardThreshold;
        localPropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
    protected void addReward(State pNextState, double pReward) {
        super.addReward(pNextState, pReward);
        //manage reward
        if (pNextState != null) {
            manageReward(pNextState, pReward);
        }
    }

    protected void manageReward(State pNextState, double pReward) {
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pNextState, pReward, localPropositionsReputation);
    }

    protected void savePropositionReputation(State pNextState,
            double pReward, Map<Proposition, Double> pPropositionsReputation) {
        if (pPropositionsReputation != null) {
            //bad reward value is distributed equally over the state`s propostions
            Collection<Proposition> props = getPropositionsForState(pNextState);
            double propReward = pReward / props.size();
            for (Proposition proposition : props) {
                Double currPropReward = pPropositionsReputation.get(proposition);
                currPropReward = currPropReward == null ? 0d : currPropReward;
                pPropositionsReputation.put(proposition, propReward + currPropReward);
            }
        }
    }

    protected Double getPropositionReputation(Proposition pProposition) {
        return localPropositionsReputation.get(pProposition);
    }

    public Map<Proposition, Double> getLocalPropositionsReputation() {
        return localPropositionsReputation;
    }

    public Map<Proposition, Double> getPropositionsReputation() {
        return localPropositionsReputation;
    }
}
