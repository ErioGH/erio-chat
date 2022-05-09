package org.erio.chat;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class ChatMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        final boolean checkType = message instanceof ObjectMessage;
        if (!checkType) return;
        final ObjectMessage objectMessage = (ObjectMessage) message;
        System.out.println();
        try {
            System.out.println(objectMessage.getObject());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}
