package com.taxtracker.repository;

import com.taxtracker.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByFormId(Long formId);
}
