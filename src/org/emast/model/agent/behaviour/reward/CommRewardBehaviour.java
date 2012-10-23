package org.emast.model.agent.behaviour.reward;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.comm.Message;
import org.emast.model.comm.MessageHistory;
import org.emast.model.comm.MessageManager;
import org.emast.model.comm.Messenger;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class CommRewardBehaviour<M extends ERG>
        implements PropRewardBehaviour<M>, Messenger {

    private final MessageHistory history;
    private final Map<Proposition, Double> messagePropositionsReputation;
    private final MessageManager messageManager;
    private final double badMessageThreshold;
    private final double messageCost;

    public CommRewardBehaviour(double pMessageCost, double pBadMessageThreshold,
            MessageManager pMessageManager) {
        messageCost = pMessageCost;
        badMessageThreshold = pBadMessageThreshold;
        messageManager = pMessageManager;
        history = new MessageHistory();
        messagePropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
    public void messageReceived(final Message pMsg) {
        Log.info("received message: " + pMsg + " from agent: " + pMsg.getSender());
        history.add(pMsg);
        M model = (M) pMsg.getAttachment("model");
        State state = (State) pMsg.getAttachment("state");
        Double reward = (Double) pMsg.getAttachment("reward");
        //save proposition reputation based on the state and reward received
        savePropositionReputation(model, state, reward, messagePropositionsReputation);
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
    public void sendMessage(final Message pMsg) {
        Log.info("sent broadcast: " + pMsg);
        Agent agent = (Agent) pMsg.getAttachment("agent");
        agent.addReward(null, messageCost);
        messageManager.broadcast(this, pMsg);
    }

    protected boolean mustSendMessage(M pModel, State pNextState, double pReward) {
        Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pNextState);

        if (props != null) {
            for (final Proposition proposition : props) {
                final Double rep = messagePropositionsReputation.get(proposition);
                if (rep <= badMessageThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void manageReward(Agent pAgent, M pModel, State pNextState, double pReward) {
        //verify the need to send message for listeners
        if (mustSendMessage(pModel, pNextState, pReward)) {
            //create the message to be sent
            final Message msg = new Message(pAgent.getNumber());
            //attach important information
            msg.putAttachment("agent", pAgent);
            msg.putAttachment("model", pModel);
            msg.putAttachment("state", pNextState);
            msg.putAttachment("reward", pReward);
            //broadcast it!
            sendMessage(msg);
        }
    }

    @Override
    public Map<Proposition, Double> getResult() {
        return messagePropositionsReputation;
    }
}
