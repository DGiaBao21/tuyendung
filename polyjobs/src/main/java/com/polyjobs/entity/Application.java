package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "jobID")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "candidateID")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "resumeID")
    private Resume resume;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String coverLetter;

    @Temporal(TemporalType.TIMESTAMP)
    private Date applyDate = new Date();

    private String status = "Chờ duyệt"; // Chờ duyệt, Đã xem, Từ chối, Trúng tuyển

    private String note;
}