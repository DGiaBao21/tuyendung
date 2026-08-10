package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class MessageDTO {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderAvatar;
    private Integer receiverId;
    private String receiverName;
    private String content;
    private Date timestamp;
    private Boolean isRead;
}
