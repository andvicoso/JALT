package org.emast.model.comm;

import java.util.Map;

public class Message {

    private Map<String, Object> attachments;
    private final int agentSender;

    public Message(final int pAgentSender) {
        this.agentSender = pAgentSender;
    }

    public Object getAttachment(String pKey) {
        return attachments.get(pKey);
    }

    public void putAttachment(String pKey, Object pAttachment) {
        attachments.put(pKey, pAttachment);
    }

    public int getSender() {
        return agentSender;
    }
}
