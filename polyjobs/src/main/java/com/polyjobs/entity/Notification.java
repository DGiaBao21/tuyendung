package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    private String title;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private Boolean isRead = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}
