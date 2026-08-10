package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class JobDTO {
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
    private Boolean status;
    private Integer companyId;
    private Integer categoryId;
}
