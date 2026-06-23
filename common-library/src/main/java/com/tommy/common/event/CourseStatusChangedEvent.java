package com.tommy.common.event;

import java.util.UUID;

public record CourseStatusChangedEvent(UUID courseId, String status) {
}
