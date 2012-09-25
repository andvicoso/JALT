package org.emast.model.comm;

/**
 *
 * @author Anderson
 */
public interface Messenger<M extends Message> {

    void messageReceived(final M pMsg);

    void sendMessage(final M pMsg);
}
