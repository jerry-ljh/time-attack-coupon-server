package com.example.couponcore.service

import com.example.couponcore.domain.UserCouponPolicyMapping
import com.example.couponcore.repository.UserCouponPolicyMappingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class UserCouponPolicyMappingService(
    private val userCouponPolicyMappingRepository: UserCouponPolicyMappingRepository,
    private val couponPolicyService: CouponPolicyService
) {

    @Transactional
    fun saveUserCouponPolicyMapping(userId: String, couponTitle: String) {
        val couponPolicyDto = couponPolicyService.findCouponPolicy(couponTitle)
        userCouponPolicyMappingRepository.save(
            UserCouponPolicyMapping(
                userId = userId,
                couponPolicy = couponPolicyService.getCouponPolicyProxy(couponPolicyDto.id),
                dateIssued = OffsetDateTime.now(),
                dateExpire = couponPolicyDto.dateExpire,
            )
        )
    }
}
