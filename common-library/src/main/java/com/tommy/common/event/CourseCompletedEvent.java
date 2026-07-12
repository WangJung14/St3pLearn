package com.tommy.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompletedEvent {
    private UUID studentId;
    private String studentEmail;
    private String studentName;
    private UUID courseId;
    private String courseTitle;
    private UUID enrollmentId;
    private LocalDateTime completedAt;
}
