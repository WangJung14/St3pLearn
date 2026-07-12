package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {

    // Use this to retrieve any answers (if available) to attach below the student's review.
    Optional findByReviewId(UUID reviewId);
}