package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.UpdateProgressRequest;
import com.tommy.learning.application.dto.response.ResumeLearningResponse;

import java.util.UUID;

public interface ILearningProgressService {
    void trackProgress(UUID studentId, UUID courseId, UUID lessonId, UpdateProgressRequest request);
    
    ResumeLearningResponse resumeLearning(UUID studentId, UUID courseId);
}
