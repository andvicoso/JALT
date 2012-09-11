package org.emast.model.comm;

import org.emast.model.agent.Agent;

public class Message<V> {

    private V value;
    private Agent sender;

    public Message() {
    }

    public Message(final V value) {
        this.value = value;
    }

    public Message(final V value, final Agent sender) {
        this.value = value;
        this.sender = sender;
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

    public Agent getSender() {
        return sender;
    }

    public void setSender(final Agent sender) {
        this.sender = sender;
    }
}
