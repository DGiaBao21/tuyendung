package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    private String salary;
    private String location;
    private String experience;
    private String workingType;
    private Integer quantity = 1;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String benefit;

    @Temporal(TemporalType.DATE)
    private Date deadline;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    private Boolean status = true; // 1: Active, 0: Closed

    @ManyToOne
    @JoinColumn(name = "companyID")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "categoryID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "employerID")
    private User employer;
}