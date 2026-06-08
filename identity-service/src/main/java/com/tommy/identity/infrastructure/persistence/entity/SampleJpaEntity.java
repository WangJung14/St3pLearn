package com.tommy.identity.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "samples")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
