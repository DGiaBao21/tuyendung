package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "SavedJobs")
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "candidateID")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "jobID")
    private Job job;

    @Temporal(TemporalType.TIMESTAMP)
    private Date saveDate = new Date();
}