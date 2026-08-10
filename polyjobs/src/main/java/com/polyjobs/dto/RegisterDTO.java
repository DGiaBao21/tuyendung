package com.polyjobs.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String phone;
    private Boolean role; // false: Ứng viên | true: Nhà tuyển dụng
    // Thông tin công ty (khi đăng ký NTD)
    private String companyName;
    private String companyAddress;
    private String companyWebsite;
}
