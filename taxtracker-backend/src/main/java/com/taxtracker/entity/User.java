package com.taxtracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "mobile_number", nullable = false, length = 10)
    private String mobileNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "address_line1", nullable = false, length = 120)
    private String addressLine1;

    @Column(name = "address_line2", nullable = false, length = 120)
    private String addressLine2;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 60)
    private String state;

    @Column(nullable = false, length = 6, columnDefinition = "CHAR(6)")
    private String pincode;
}
