package com.tommy.admin.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_revenues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private LocalDate date;
    
    private BigDecimal totalRevenue;
}
