package com.taxtracker.repository;

import com.taxtracker.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    boolean existsByFormId(Long formId);
}
