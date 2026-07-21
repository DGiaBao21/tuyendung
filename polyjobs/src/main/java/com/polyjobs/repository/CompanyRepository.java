package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polyjobs.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findFirstByEmployer(com.polyjobs.entity.User employer);
}