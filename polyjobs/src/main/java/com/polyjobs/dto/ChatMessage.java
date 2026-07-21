package com.polyjobs.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private Integer senderId;
    private Integer receiverId;
    private String content;
}
