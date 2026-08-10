package com.polyjobs.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String profession;
    private Boolean role;
    private Boolean isAdmin;
    private Boolean isActive;
    // Không chứa password
}
