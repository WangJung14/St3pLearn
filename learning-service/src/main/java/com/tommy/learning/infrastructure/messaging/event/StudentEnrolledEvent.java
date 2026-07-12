package com.tommy.learning.infrastructure.messaging.event;

import java.util.UUID;

public record StudentEnrolledEvent(UUID studentId, UUID courseId) {
}