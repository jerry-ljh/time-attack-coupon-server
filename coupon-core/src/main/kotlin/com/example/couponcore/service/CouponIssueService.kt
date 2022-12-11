package com.example.couponcore.service

import com.example.couponcore.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CouponIssueService(
    private val distributedLockService: DistributedLockService,
    private val userCouponPolicyMappingService: UserCouponPolicyMappingService,
    private val couponPolicyService: CouponPolicyService,
    private val redisRepository: RedisRepository
) {

    private val log = LoggerFactory.getLogger(this::class.simpleName)


    fun issue(key: String, userId: String) {
        val remainCoupon = distributedLockService.executeWithLock(
            lockName = "${key}_issue_lock",
            waitSeconds = 3,
            leaseSeconds = 3
        ) {
            markIssueStatus(key, userId)
        }
        if (remainCoupon) syncCouponIssueStatus(userId, key)
    }

    fun markIssueStatus(key: String, userId: String): Boolean {
        if (getCouponIssueCount(key) >= getTotalCouponQuantity(key)) return false
        val isSuccessIssueTrueMarking = addIssueMarking(key, userId)
        if (isSuccessIssueTrueMarking.not()) {
            log.info("key : $key , userId: $userId 발급 마킹 실패 (중복 요청)")
        }
        return isSuccessIssueTrueMarking

    }

    fun getCouponIssueCount(key: String): Long {
        return redisRepository.sCard(key)!!
    }

    fun getTotalCouponQuantity(key: String): Long {
        return couponPolicyService.findCouponPolicy(key).quantity
    }

    fun addIssueMarking(key: String, userId: String): Boolean {
        log.info("issue marking 요청 key : $key , userId: $userId ")
        return redisRepository.sAdd(key, userId) == 1L
    }

    fun rollbackIssueMarking(key: String, userId: String): Boolean {
        log.info("[ROLLBACK] SREM 요청 key : $key , userId: $userId ")
        return redisRepository.sRem(key, userId) == 1L
    }

    fun syncCouponIssueStatus(userId: String, couponTitle: String) {
        try {
            userCouponPolicyMappingService.saveUserCouponPolicyMapping(userId, couponTitle)
        } catch (e: Exception) {
            rollbackIssueMarking(couponTitle, userId)
        }
    }
}