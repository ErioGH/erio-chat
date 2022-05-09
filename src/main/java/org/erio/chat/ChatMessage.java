package org.erio.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage implements Serializable {

    private String message;

    private String loginFrom;

    private String messageType;

    @Override
    public String toString() {
        if (":PRIVATE:".equals(messageType)) return "PRIVATE> " + loginFrom + ": " + message;
        return loginFrom + ": " + message;
    }

}
