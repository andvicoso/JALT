package org.emast.model.comm;

/**
 *
 * @author Anderson
 */
public interface Messenger {

    void messageReceived(final Message pMsg);

    void sendMessage(final Message pMsg);
}
