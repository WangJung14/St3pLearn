package com.tommy.admin.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_replicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReplica {
    @Id
    private UUID id;
    
    private String email;
    private String fullName;
    private String role;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
