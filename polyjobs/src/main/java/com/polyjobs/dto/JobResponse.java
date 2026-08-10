package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class JobResponse {
    private Integer id;
    private String title;
    private String description;
    private String salary;
    private String location;
    private String experience;
    private String workingType;
    private Integer quantity;
    private String benefit;
    private Date deadline;
    private Date createdDate;
    private Boolean status;
    private Boolean isHidden;
    // Thông tin liên quan (flatten từ Entity)
    private String companyName;
    private String companyLogo;
    private Integer companyId;
    private String categoryName;
    private Integer categoryId;
    private String employerName;
    private Integer employerId;
}
