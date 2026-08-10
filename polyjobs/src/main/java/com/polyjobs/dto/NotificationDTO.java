package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Boolean isRead;
    private Date createdDate;
}
