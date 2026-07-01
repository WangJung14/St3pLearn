package com.tommy.learning.application.service;

import java.util.UUID;

public interface ILearningService {

    // start learning course
    public UUID startLearning(UUID studentId, UUID courseId);
}
