package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ResumeDTO {
    private Integer id;
    private String fileName;
    private String title;
    private Date uploadDate;
    private Integer candidateId;
    private String candidateName;
}
