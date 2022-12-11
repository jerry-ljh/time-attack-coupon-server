package com.example.couponcore.service

import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.repository.CouponPolicyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponPolicyService(
    private val couponPolicyRepository: CouponPolicyRepository
) {

    @Transactional(readOnly = true)
    fun findCouponPolicy(title: String): CouponPolicyDto {
        return couponPolicyRepository.findCouponPolicyDto(title)
    }

    fun getCouponPolicyProxy(id: Long): CouponPolicy {
        return couponPolicyRepository.getReferenceById(id)
    }
}
