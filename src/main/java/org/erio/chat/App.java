package org.erio.chat;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) throws IOException, JMSException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("---Welcome to JMS Chat---");
            String login = "";
            while (login.isEmpty()) {
                System.out.print("Insert you login: ");
                login = bufferedReader.readLine();
            }
            System.out.println("Hello " + login + ".");
            System.out.println("For private massages add @<username to> before message body.");
            Chat chat = new Chat(login);
            chat.init();
            while (true) {
                String message = bufferedReader.readLine();
                if ("exit".equals(message)) break;
                String firstWords = message.split(" ")[0];
                if (firstWords.startsWith("@")) {
                    message = message.substring(firstWords.length(), message.length() -1);
                    chat.sendPrivate(message, firstWords.substring(1));
                } else {
                    chat.sendBroadcast(message);
                }
            }
            chat.stop();
        }
    }

}
