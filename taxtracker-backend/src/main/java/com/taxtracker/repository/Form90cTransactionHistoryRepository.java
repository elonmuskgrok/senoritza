package com.taxtracker.repository;

import com.taxtracker.entity.Form90cTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Form90cTransactionHistoryRepository extends JpaRepository<Form90cTransactionHistory, Long> {
    List<Form90cTransactionHistory> findByFormId(Long formId);
    boolean existsByFormId(Long formId);
    void deleteByFormId(Long formId);
}
