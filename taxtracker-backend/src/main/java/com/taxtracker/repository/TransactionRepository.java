package com.taxtracker.repository;

import com.taxtracker.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:financialYear IS NULL OR t.financialYear = :financialYear) " +
           "AND (:month IS NULL OR t.txnMonth = :month) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:organizationName IS NULL OR LOWER(t.organizationName) LIKE LOWER(CONCAT('%', :organizationName, '%')))")
    Page<Transaction> findTransactionsWithFilters(
            @Param("userId") Long userId,
            @Param("financialYear") String financialYear,
            @Param("month") Integer month,
            @Param("type") String type,
            @Param("organizationName") String organizationName,
            Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:financialYear IS NULL OR t.financialYear = :financialYear) " +
           "AND (:month IS NULL OR t.txnMonth = :month) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:organizationName IS NULL OR LOWER(t.organizationName) LIKE LOWER(CONCAT('%', :organizationName, '%')))")
    List<Transaction> findAllTransactionsWithFilters(
            @Param("userId") Long userId,
            @Param("financialYear") String financialYear,
            @Param("month") Integer month,
            @Param("type") String type,
            @Param("organizationName") String organizationName
    );
}
