package org.erio.chat;

import lombok.Getter;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.jetbrains.annotations.NotNull;

import javax.jms.*;

@Getter
public class Chat {

    public static final String URL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static final String BROADCAST = ":BROADCAST:";

    public static final String PRIVATE = ":PRIVATE:";

    private ActiveMQConnectionFactory factory;

    private Connection connection;

    private Session session;

    private Destination destinationBroadcast;

    private MessageConsumer consumerBroadcast;

    private MessageProducer producerBroadcast;

    private Destination destinationPrivate;

    private MessageConsumer consumerPrivate;

    private final String login;


    public Chat(String login) {
        this.login = login;
    }

    public void init() throws Exception {
        final BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        factory = new ActiveMQConnectionFactory(URL);
        factory.setTrustAllPackages(true);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        final ChatMessageListener listener = new ChatMessageListener();

        destinationBroadcast = session.createTopic(BROADCAST);
        producerBroadcast = session.createProducer(destinationBroadcast);
        consumerBroadcast = session.createConsumer(destinationBroadcast);
        consumerBroadcast.setMessageListener(listener);

        destinationPrivate = session.createQueue(PRIVATE + login);
        consumerPrivate = session.createConsumer(destinationPrivate);
        consumerPrivate.setMessageListener(listener);
    }

    public void sendBroadcast(final @NotNull String message) throws JMSException {
        if (message.isEmpty()) return;
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setLoginFrom(login);
        chatMessage.setMessageType(BROADCAST);
        producerBroadcast.send(session.createObjectMessage(chatMessage));
    }

    public void sendPrivate(final @NotNull String message, String loginTo) throws JMSException {
        if (message.isEmpty()) return;
        final String destinationName = PRIVATE + loginTo;
        final Destination destination = session.createQueue(destinationName);
        final MessageProducer producerPrivate = session.createProducer(destination);
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setLoginFrom(login);
        chatMessage.setMessageType(PRIVATE);
        producerPrivate.send(session.createObjectMessage(chatMessage));
    }

    public void stop() throws JMSException {
        session.close();
        connection.close();
    }

}
