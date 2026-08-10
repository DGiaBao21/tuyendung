package com.polyjobs.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ApplicationDTO {
    private Integer id;
    private Integer jobId;
    private String jobTitle;
    private Integer candidateId;
    private String candidateName;
    private Integer resumeId;
    private String resumeFileName;
    private String coverLetter;
    private Date applyDate;
    private String status;
    private String note;
}
