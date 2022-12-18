package com.example.couponcore.service

import com.example.couponcore.config.COUPON_POLICY_DTO
import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.repository.CouponPolicyRepository
import org.springframework.aop.framework.AopContext
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class CouponPolicyService(
    private val couponPolicyRepository: CouponPolicyRepository
) {
    private val self: CouponPolicyService by lazy { AopContext.currentProxy() as CouponPolicyService }

    @Cacheable(cacheNames = [COUPON_POLICY_DTO])
    @Transactional(readOnly = true)
    fun findCouponPolicy(title: String): CouponPolicyDto {
        return couponPolicyRepository.findCouponPolicyDto(title)
    }

    fun getCouponPolicyProxy(id: Long): CouponPolicy {
        return couponPolicyRepository.getProxy(id)
    }

    fun isIssuableDate(title: String): Boolean {
        val couponPolicyDto = self.findCouponPolicy(title)
        val now = OffsetDateTime.now()
        val issueStart = couponPolicyDto.dateIssueStart
        val issueEnd = couponPolicyDto.dateIssueEnd
        return issueStart <= now && now <= issueEnd
    }
}
