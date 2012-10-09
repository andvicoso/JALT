package org.emast.model.agent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.comm.MessageHistory;
import org.emast.model.comm.MessageManager;
import org.emast.model.comm.Messenger;
import org.emast.model.comm.StateRewardMessage;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.rewardcombinator.RewardCombinator;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class CommChangePreservAgent<M extends ERG> extends ChangePreservGoalPropRepAgent<M>
        implements Messenger<StateRewardMessage> {

    private final MessageHistory history;
    private final Map<Proposition, Double> messagePropositionsReputation;
    private final RewardCombinator rewardCombinator;
    private final MessageManager messageManager;
    private final double badMessageThreshold;
    private final double messageCost;

    public CommChangePreservAgent(int pAgent, double pMessageCost, double pBadRewardThreshold,
            double pBadMessageThreshold, RewardCombinator pRewardCombinator, MessageManager pMessageManager) {
        super(pAgent, pBadRewardThreshold);
        messageCost = pMessageCost;
        badMessageThreshold = pBadMessageThreshold;
        rewardCombinator = pRewardCombinator;
        messageManager = pMessageManager;
        history = new MessageHistory();
        messagePropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
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
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage(final StateRewardMessage pMsg) {
        print("sent broadcast: " + pMsg);
        addReward(null, messageCost);
        messageManager.broadcast(this, pMsg);
    }

    protected boolean mustSendMessage(final State state) {
        boolean somePropChanged = false;
        final Collection<Proposition> props = getPropositionsForState(state);

        if (props != null) {
            for (final Proposition proposition : props) {
                final Double rep = getPropositionReputation(proposition);
                if (rep <= badMessageThreshold) {
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
    protected void manageReward(State nextState, double reward) {
        super.manageReward(nextState, reward);
        //verify the need to send message for listeners
        if (mustSendMessage(nextState)) {
            //create the message to be sent
            final StateRewardMessage msg = new StateRewardMessage(nextState, reward, getAgent());
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
