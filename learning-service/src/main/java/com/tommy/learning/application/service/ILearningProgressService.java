package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.UpdateProgressRequest;

import java.util.UUID;

public interface ILearningProgressService {
    void trackProgress(UUID studentId, UUID courseId, UUID lessonId, UpdateProgressRequest request);
}
