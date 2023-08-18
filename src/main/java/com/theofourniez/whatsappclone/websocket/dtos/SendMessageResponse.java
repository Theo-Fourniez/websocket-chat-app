package com.theofourniez.whatsappclone.websocket.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * A record used to form a response to the client
 * Is sent back to the client as a JSON string
 */
public record SendMessageResponse(String message, String from) {
    /**
     * Returns a JSON string representation of the object
     */
    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
