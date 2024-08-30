package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class ChatMessageRequest {
    private String model;
    private List<ChatMessage> messages;
    @Setter
    @Getter
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
