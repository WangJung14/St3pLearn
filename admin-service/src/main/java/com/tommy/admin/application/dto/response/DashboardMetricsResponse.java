package com.tommy.admin.application.dto.response;

import com.tommy.admin.domain.entity.CourseReplica;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardMetricsResponse {
    private BigDecimal monthlyRevenue;
    private Long newStudentsToday;
    private Long activeUsersLast7Days;
    private List<CourseReplica> topCourses;
    private Long pendingReports;
}
