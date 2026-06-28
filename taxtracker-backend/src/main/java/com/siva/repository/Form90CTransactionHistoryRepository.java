package com.siva.repository;

import com.siva.entity.Form90CTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Form90CTransactionHistoryRepository extends JpaRepository<Form90CTransactionHistory, Long> {
    List<Form90CTransactionHistory> findByFormId(Long formId);
    boolean existsByFormId(Long formId);
    void deleteByFormId(Long formId);
}
