package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String companyName;

    private String logo;
    private String website;
    private String address;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "employerID")
    private User employer;
}