package com.siva.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false, unique = true)
    private Form90C form;

    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";

    @Column(name = "confirmation_message", nullable = false, length = 255)
    private String confirmationMessage = "Form 90C submitted successfully";

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
}
