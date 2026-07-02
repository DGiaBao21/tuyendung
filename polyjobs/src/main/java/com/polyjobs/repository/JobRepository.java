package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.polyjobs.entity.Job;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    // Tìm việc làm theo keyword trong title
    List<Job> findByTitleContainingIgnoreCase(String keyword);

    // Tìm việc làm theo tỉnh thành
    List<Job> findByLocation(String location);

    // Tìm việc làm theo keyword + tỉnh thành
    List<Job> findByTitleContainingIgnoreCaseAndLocation(String keyword, String location);

    // Lấy danh sách việc làm theo công ty
    List<Job> findByCompanyId(Integer companyId);
}