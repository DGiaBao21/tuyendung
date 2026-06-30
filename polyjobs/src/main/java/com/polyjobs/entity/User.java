package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullname;

    @Column(unique = true)
    private String email;

    private String phone;
    private String address;
    private String avatar;

    // 0: Candidate | 1: Employer/Admin
    private Boolean role = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}