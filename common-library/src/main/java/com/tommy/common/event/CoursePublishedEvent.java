package com.tommy.common.event;

import java.util.List;
import java.util.UUID;

public record CoursePublishedEvent(
        UUID courseId,
        String status,
        Integer totalLessons,
        Integer totalDuration,
        List<UUID> lessonIds )
{ }