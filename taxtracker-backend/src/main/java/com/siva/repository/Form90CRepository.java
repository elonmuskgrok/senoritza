package com.siva.repository;

import com.siva.entity.Form90C;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form90CRepository extends JpaRepository<Form90C, Long> {
    Optional<Form90C> findByIdAndUserId(Long id, Long userId);
    Optional<Form90C> findByUserEmail(String email);
    Optional<Form90C> findByUserEmailAndStatus(String email, String status);
    Optional<Form90C> findByUserEmailAndFinancialYear(String email, String financialYear);
}
