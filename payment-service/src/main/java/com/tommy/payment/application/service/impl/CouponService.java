package com.tommy.payment.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.payment.application.dto.request.CouponRequest;
import com.tommy.payment.application.dto.request.ApplyCouponRequest;
import com.tommy.payment.domain.entity.Coupon;
import com.tommy.payment.domain.enums.DiscountType;
import com.tommy.payment.infrastructure.persistence.repository.CouponRepository;
import com.tommy.payment.infrastructure.persistence.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Transactional
    public Coupon createCoupon(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .courseId(request.getCourseId())
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(UUID studentId, ApplyCouponRequest request) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        // Validate dates
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new AppException(ErrorCode.COUPON_NOT_STARTED);
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new AppException(ErrorCode.COUPON_EXPIRED);
        }

        // Validate usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new AppException(ErrorCode.COUPON_LIMIT_REACHED);
        }

        // Validate course specific
        if (coupon.getCourseId() != null && !coupon.getCourseId().equals(request.getCourseId())) {
            throw new AppException(ErrorCode.COUPON_INVALID_COURSE);
        }

        // Validate if already used by this student
        if (couponUsageRepository.existsByCouponIdAndStudentId(coupon.getId(), studentId)) {
            throw new AppException(ErrorCode.COUPON_ALREADY_USED);
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = request.getOriginalAmount().multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        }
        
        // Discount cannot exceed original amount
        if (discount.compareTo(request.getOriginalAmount()) > 0) {
            discount = request.getOriginalAmount();
        }

        return discount;
    }
}
