package org.emast.model.comm;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Anderson
 */
public class MessageManager {

    private Collection<Messenger> messengers;

    public MessageManager() {
        messengers = new ArrayList<Messenger>();
    }

    public void add(Messenger pMessenger) {
        messengers.add(pMessenger);
    }

    public void remove(Messenger pMessenger) {
        messengers.remove(pMessenger);
    }

    public void broadcast(Messenger pSender, Message pMsg) {
        for (Messenger m : messengers) {
            if (!m.equals(pSender)) {
                m.messageReceived(pMsg);
            }
        }
    }
}
