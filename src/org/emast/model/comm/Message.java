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

    public void attach(String pKey, Object pAttachment) {
        attachments.put(pKey, pAttachment);
    }

    public void attachAll(Map<String, Object> pAttachments) {
        attachments.putAll(pAttachments);
    }

    public int getSender() {
        return agentSender;
    }
}
