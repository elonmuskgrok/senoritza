package com.taxtracker.repository;

import com.taxtracker.entity.Form90c;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Form90cRepository extends JpaRepository<Form90c, Long> {
    Optional<Form90c> findByIdAndUserId(Long id, Long userId);
    Optional<Form90c> findByUserEmail(String email);
    Optional<Form90c> findByUserEmailAndStatus(String email, String status);
}
