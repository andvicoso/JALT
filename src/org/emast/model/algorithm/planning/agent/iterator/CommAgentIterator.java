package org.emast.model.algorithm.planning.agent.iterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.emast.model.algorithm.planning.rewardcombinator.RewardCombinator;
import org.emast.model.comm.Message;
import org.emast.model.comm.MessageHistory;
import org.emast.model.comm.StateRewardMessage;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class CommAgentIterator<M extends ERG> extends ChangePreservGoalPropRepAgentIterator<M> {

    private final double badMessageThreshold;
    private final MessageHistory history;
    private final Map<Proposition, Double> messagePropositionsReputation;
    private final double messageCost;
    private final RewardCombinator rewardCombinator;

    public CommAgentIterator(int pAgent, double pMessageCost, double pBadRewardThreshold,
            double pBadMessageThreshold, RewardCombinator pRewardCombinator) {
        super(pAgent, pBadRewardThreshold);
        messageCost = pMessageCost;
        badMessageThreshold = pBadMessageThreshold;
        rewardCombinator = pRewardCombinator;
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
            try {
                //verify the need to change the preservation goal
                final Policy p = changePreservationGoal(pMsg.getState());
                //if found a policy
                if (p != null) {
                    //changed preservation goal, continue iteration 
                    //with the new preservation goal and policy
                    setPolicy(p);
                }
            } catch (InvalidExpressionException ex) {
                Logger.getLogger(CommAgentIterator.class.getName()).log(Level.SEVERE, null, ex);
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
            //create the message to be sent
            final Message msg = new StateRewardMessage(nextState, reward, getAgent());
            //broadcast it!
            sendMessage(msg);
        }
    }

    public Map<Proposition, Double> getMessagePropositionsReputation() {
        return messagePropositionsReputation;
    }

    @Override
    public Map<Proposition, Double> getPropositionsReputation() {
        Collection<Map<Proposition, Double>> reps =
                Arrays.asList(getLocalPropositionsReputation(), getMessagePropositionsReputation());
        return rewardCombinator.combine(reps);
    }

    public RewardCombinator getRewardCombinator() {
        return rewardCombinator;
    }
}
