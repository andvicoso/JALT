package org.emast.model.agent.behavior.individual.reward;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.agent.Agent;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class PropRepReward implements PropReward {

    protected double badRewardThreshold;
    protected Map<Proposition, Double> localPropositionsReputation;

    public PropRepReward(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
        localPropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
    public void behave(Agent pAgent, Problem<ERG> pProblem, Map<String, Object> pParameters) {
        State pNextState = (State) pParameters.get("state");
        Double pReward = (Double) pParameters.get("reward");
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pProblem.getModel(), pNextState, pReward, localPropositionsReputation);
    }

    private void savePropositionReputation(ERG pModel, State pNextState,
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
