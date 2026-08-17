package com.polyjobs.repository;

import com.polyjobs.entity.Application;
import com.polyjobs.entity.Job;
import com.polyjobs.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByCandidate(User candidate);
    Page<Application> findByCandidate(User candidate, Pageable pageable);
    List<Application> findByJob(Job job);
    List<Application> findByJob_Employer(User employer);
    boolean existsByResume(com.polyjobs.entity.Resume resume);
    long countByJob(Job job);
}
