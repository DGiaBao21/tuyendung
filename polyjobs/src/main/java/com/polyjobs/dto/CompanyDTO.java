package com.polyjobs.dto;

import lombok.Data;

@Data
public class CompanyDTO {
    private Integer id;
    private String companyName;
    private String logo;
    private String website;
    private String address;
    private String description;
    private Integer employerId;
}
