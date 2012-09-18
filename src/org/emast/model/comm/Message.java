package org.emast.model.comm;

public class Message<V> {

    private V value;
    private int agentSender;

    public Message() {
    }

    public Message(final V value) {
        this.value = value;
    }

    public Message(final V value, final int pAgentSender) {
        this.value = value;
        this.agentSender = pAgentSender;
    }

    public V getValue() {
        return value;
    }

    public void setValue(final V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue() != null ? getValue().toString() : "";
    }

    public int getSender() {
        return agentSender;
    }

    public void setSender(final int pAgentSender) {
        agentSender = pAgentSender;
    }
}
