package com.polyjobs.repository;

import com.polyjobs.entity.Resume;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findByCandidateOrderByUploadDateDesc(User candidate);
}
