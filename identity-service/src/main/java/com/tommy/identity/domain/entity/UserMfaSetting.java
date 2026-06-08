package com.tommy.identity.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_mfa_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMfaSetting {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private Account account;

    @Column(name = "secret_key", length = 255)
    private String secretKey;

    @Column(nullable = false)
    private boolean enabled = false;

    // Chuyển List Java thành kiểu JSONB dưới PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recovery_codes", columnDefinition = "jsonb")
    private List<String> recoveryCodes;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}