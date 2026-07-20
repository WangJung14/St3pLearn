package com.tommy.admin.application.service;

import com.tommy.admin.application.dto.response.DashboardMetricsResponse;
import com.tommy.admin.domain.entity.CourseReplica;
import com.tommy.admin.domain.entity.DailyRevenue;
import com.tommy.admin.infrastructure.persistence.repository.CourseReplicaRepository;
import com.tommy.admin.infrastructure.persistence.repository.DailyRevenueRepository;
import com.tommy.admin.infrastructure.persistence.repository.UserReplicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final UserReplicaRepository userReplicaRepository;
    private final CourseReplicaRepository courseReplicaRepository;
    private final DailyRevenueRepository dailyRevenueRepository;

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics() {
        // Total monthly revenue
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal monthlyRevenue = dailyRevenueRepository.findAll().stream()
                .filter(r -> !r.getDate().isBefore(startOfMonth))
                .map(DailyRevenue::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        // New students today
        LocalDate today = LocalDate.now();
        long newStudentsToday = userReplicaRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().isEqual(today))
                .count();

        // Best selling courses (top 5)
        List<CourseReplica> topCourses = courseReplicaRepository.findAll().stream()
                .sorted((c1, c2) -> Long.compare(c2.getEnrollmentCount(), c1.getEnrollmentCount()))
                .limit(5)
                .toList();
                
        // Active Users (logged in within last 7 days)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long activeUsers = userReplicaRepository.findAll().stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(sevenDaysAgo))
                .count();

        return DashboardMetricsResponse.builder()
                .monthlyRevenue(monthlyRevenue)
                .newStudentsToday(newStudentsToday)
                .activeUsersLast7Days(activeUsers)
                .topCourses(topCourses)
                .pendingReports(0L) // Phase 2 stub
                .build();
    }
}
