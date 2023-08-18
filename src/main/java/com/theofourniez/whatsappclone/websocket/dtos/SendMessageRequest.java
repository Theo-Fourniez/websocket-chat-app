package com.theofourniez.whatsappclone.websocket.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A record used to parse the JSON message sent by the client
 */
public record SendMessageRequest(String message, String to) {
    /**
     * Parses the JSON message sent by the client
     * @param message The JSON message sent by the client
     * @return A SendMessageRequest object
     * @throws JsonProcessingException in case the JSON message is malformed
     */
    public static SendMessageRequest parse(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, SendMessageRequest.class);
    }
}
