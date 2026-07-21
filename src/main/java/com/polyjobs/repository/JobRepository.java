package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Lấy danh sách việc làm theo employer
    List<Job> findByEmployer(com.polyjobs.entity.User employer);

    // Tìm kiếm tổng hợp
    @Query("SELECT j FROM Job j WHERE j.status = true AND j.isHidden = false AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR j.location = :location) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId) AND " +
           "(:salary IS NULL OR :salary = '' OR j.salary LIKE CONCAT('%', :salary, '%'))")
    List<Job> searchJobs(@Param("keyword") String keyword, 
                         @Param("location") String location, 
                         @Param("categoryId") Integer categoryId, 
                         @Param("salary") String salary);
}