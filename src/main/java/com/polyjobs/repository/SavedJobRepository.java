package com.polyjobs.repository;

import com.polyjobs.entity.Job;
import com.polyjobs.entity.SavedJob;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Integer> {
    List<SavedJob> findByCandidate(User candidate);
    Optional<SavedJob> findByCandidateAndJob(User candidate, Job job);
}
