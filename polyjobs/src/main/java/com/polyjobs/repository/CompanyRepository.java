package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.polyjobs.entity.Category;
import com.polyjobs.entity.Job;

@Repository
public interface CompanyRepository extends JpaRepository<Category, Integer> {
    // Không cần viết code gì bên trong. Spring đã tự làm sẵn các hàm findAll(),
    // findById(), save(), delete()...
}