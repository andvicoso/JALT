package org.emast.model.comm;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Anderson
 */
public class MessageManager {

    private final Collection<Messenger> messengers;
    private final boolean sendToSender;

    public MessageManager(boolean pSendToSender) {
        messengers = new ArrayList<Messenger>();
        sendToSender = pSendToSender;
    }

    public void add(Messenger pMessenger) {
        messengers.add(pMessenger);
    }

    public void remove(Messenger pMessenger) {
        messengers.remove(pMessenger);
    }

    public void broadcast(Messenger pSender, Message pMsg) {
        for (Messenger m : messengers) {
            if ((!sendToSender && !m.equals(pSender)) || sendToSender) {
                m.messageReceived(pMsg);
            }
        }
    }
}
