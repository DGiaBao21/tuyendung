package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "candidateID")
    private User candidate;

    private String fileName;
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate = new Date();
}