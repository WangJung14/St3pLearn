package com.tommy.learning.application.service;

import com.tommy.common.event.CoursePublishedEvent;
import com.tommy.common.event.CourseStatusChangedEvent;

public interface ICourseSyncService {

    // dong bo hoa du lieu publish course
    public void syncPublishedCourse(CoursePublishedEvent event);

    // dong bo hoa update status
    public void syncCourseStatus(CourseStatusChangedEvent event);
}
