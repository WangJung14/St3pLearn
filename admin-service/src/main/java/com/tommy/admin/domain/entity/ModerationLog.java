package com.tommy.admin.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "moderation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID caseId;

    @Column(nullable = false)
    private UUID adminId;

    @Column(nullable = false)
    private String actionTaken; // e.g., CASE_OPENED, DISMISSED, SUSPEND_USER, HIDE_COURSE

    @Column(length = 4000)
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
