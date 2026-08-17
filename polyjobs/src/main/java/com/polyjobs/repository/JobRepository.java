package com.polyjobs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.polyjobs.entity.Job;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findByTitleContainingIgnoreCase(String keyword);
    List<Job> findByLocation(String location);
    List<Job> findByTitleContainingIgnoreCaseAndLocation(String keyword, String location);
    List<Job> findByCompanyId(Integer companyId);
    List<Job> findByEmployer(com.polyjobs.entity.User employer);

    // Tim kiem tong hop - tra ve List (cho filter count)
    @Query("SELECT j FROM Job j WHERE j.status = true AND j.isHidden = false AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR j.location = :location) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId) AND " +
           "(:salary IS NULL OR :salary = '' OR j.salary LIKE CONCAT('%', :salary, '%'))")
    List<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("categoryId") Integer categoryId,
                         @Param("salary") String salary);

    // Tim kiem tong hop - tra ve Page (phan trang)
    @Query("SELECT j FROM Job j WHERE j.status = true AND j.isHidden = false AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR j.location = :location) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId) AND " +
           "(:salary IS NULL OR :salary = '' OR j.salary LIKE CONCAT('%', :salary, '%'))")
    Page<Job> searchJobsPaged(@Param("keyword") String keyword,
                              @Param("location") String location,
                              @Param("categoryId") Integer categoryId,
                              @Param("salary") String salary,
                              Pageable pageable);
}
