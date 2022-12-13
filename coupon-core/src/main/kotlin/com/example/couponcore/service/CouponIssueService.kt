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

    companion object {
        fun getIssuedCouponSetKey(key: String) = "${key}_issue_set"
    }

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    fun issue(key: String, userId: String) {
        val issuable = distributedLockService.executeWithLock(
            lockName = "${key}_issue_lock",
            waitSeconds = 3,
            leaseSeconds = 3
        ) {
            markIssueStatus(key, userId)
        }
        if (issuable) syncCouponIssueStatus(key, userId)
    }

    fun markIssueStatus(key: String, userId: String): Boolean {
        val issuedCouponSetKey = getIssuedCouponSetKey(key)
        if (getCouponIssueCount(issuedCouponSetKey) >= getTotalCouponQuantity(key)) return false
        val isSuccessIssueTrueMarking = addIssueMarking(issuedCouponSetKey, userId)
        if (isSuccessIssueTrueMarking.not()) {
            log.info("key : $issuedCouponSetKey , userId: $userId 발급 마킹 실패 (중복 요청)")
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

    fun syncCouponIssueStatus(couponTitle: String, userId: String) {
        try {
            userCouponPolicyMappingService.saveUserCouponPolicyMapping(userId, couponTitle)
            log.info("발급 완료 couponTitle: $couponTitle, userId: $userId")
        } catch (e: Exception) {
            log.error("발급 실패 couponTitle: $couponTitle, userId: $userId", e)
            rollbackIssueMarking(getIssuedCouponSetKey(couponTitle), userId)
        }
    }
}
