package org.emast.model.algorithm.planning.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.emast.model.agent.Agent;
import org.emast.model.comm.Message;
import org.emast.model.comm.MessageHistory;
import org.emast.model.comm.StateRewardMessage;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class CommAgentIterator<M extends MDP & ERG> extends PropReputationAgentIterator<M> {

    private final double badMessageThreshold;
    private final MessageHistory history;
    private final Map<Proposition, Double> messagePropositionsReputation;
    private final double messageCost;

    public CommAgentIterator(M pModel, Policy pInitialPolicy, int pAgent, State pInitialState,
            double pMessageCost, double pBadRewardThreshold, double pBadMessageThreshold) {
        super(pModel, pInitialPolicy, pAgent, pInitialState, pBadRewardThreshold);
        messageCost = pMessageCost;
        badMessageThreshold = pBadMessageThreshold;
        history = new MessageHistory();
        messagePropositionsReputation = new HashMap<Proposition, Double>();
    }

    public void messageReceived(final StateRewardMessage pMsg) {
        print("received message: " + pMsg + " from agent: " + pMsg.getSender());
        history.add(pMsg);
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pMsg.getState(), pMsg.getValue(), messagePropositionsReputation);
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pMsg.getState())) {
            //verify the need to change the preservation goal
            final Policy p = changePreservationGoal(pMsg.getState());
            //if found a policy
            if (p != null) {
                //changed preservation goal, continue iteration 
                //with the new preservation goal and policy
                setPolicy(p);
            }
        }
    }

    protected void sendMessage(final Message pMsg) {
        print("sent broadcast: " + pMsg);
        addReward(null, messageCost);
    }

    protected boolean mustSendMessage(final State state) {
        boolean somePropChanged = false;
        final Collection<Proposition> props = getPropositionsForState(state);

        if (props != null) {
            for (final Proposition proposition : props) {
                final Double rep = getPropositionReputation(proposition);
                if (rep < badMessageThreshold) {
                    somePropChanged = true;
                    break;
                }
            }
        }
        //send message if some proposition changed
        return somePropChanged;
    }

    @Override
    protected Double getPropositionReputation(final Proposition pProposition) {
        final Double localValue = super.getPropositionReputation(pProposition);
        final Double externalValue = messagePropositionsReputation.get(pProposition);

        if (localValue != null && externalValue != null) {
            return (localValue + externalValue) / 2;
        } else if (localValue != null) {
            return localValue;
        } else if (externalValue != null) {
            return externalValue;
        }
        //if doesn't exist
        return null;
    }

    @Override
    protected void manageBadReward(State nextState, double reward) {
        super.manageBadReward(nextState, reward);
        //verify the need to send message for listeners
        if (mustSendMessage(nextState)) {
            final Agent ag = getModel().getAgents().get(getAgent());
            //create the message to be sent
            final Message msg = new StateRewardMessage(nextState, reward, ag);
            //broadcast it!
            sendMessage(msg);
        }
    }

    public static class CommAgentIteratorFactory<M extends MDP & ERG> extends PropReputationAgentIteratorFactory<M> {

        protected double badMessageThreshold;
        private final double messageCost;

        public CommAgentIteratorFactory(double pMessageCost, double pBadRewardThreshold, double pBadMessageThreshold) {
            super(pBadRewardThreshold);
            badMessageThreshold = pBadMessageThreshold;
            messageCost = pMessageCost;
        }

        @Override
        public List<AgentIterator> createAgentIterators(Problem<M> pProblem, Policy pInitialPolicy) {
            return super.createAgentIterators(pProblem, pInitialPolicy);
        }

        @Override
        public CommAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
            final M newModel = (M) pModel.copy();
            return new CommAgentIterator(newModel, pPolicy, pAgent, pInitialState,
                    messageCost, badRewardThreshold, badMessageThreshold);
        }
    }
}