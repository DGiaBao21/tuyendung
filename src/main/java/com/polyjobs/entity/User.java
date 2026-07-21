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
    private String profession; // Ngành nghề / Chuyên ngành

    // 0: Candidate | 1: Employer/Admin
    private Boolean role = false;

    // Phân quyền quản trị viên
    private Boolean isAdmin = false;

    // Trạng thái tài khoản (true: Hoạt động, false: Bị chặn)
    private Boolean isActive = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}