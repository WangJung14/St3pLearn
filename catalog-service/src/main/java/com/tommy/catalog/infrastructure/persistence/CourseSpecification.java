package com.tommy.catalog.infrastructure.persistence;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.Category;
import com.tommy.catalog.domain.enums.CourseStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;
public class CourseSpecification {

    // 1. Only take PUBLISHED courses
    public static Specification<Course> isPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), CourseStatus.PUBLISHED);
    }

    // 2. Find by Title (LIKE %keyword%)
    public static Specification<Course> hasTitleLike(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }

    // 3. Filter by Level
    public static Specification<Course> hasLevel(String level) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(level)) return null;
            return criteriaBuilder.equal(root.get("level"), level);
        };
    }

    // 4. Filter by Price
    public static Specification<Course> isPriceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    // 5. Filter by Category (Join table)
    public static Specification<Course> hasCategoryId(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) return null;
            Join<Course, Category> categoryJoin = root.join("categories");
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }
}