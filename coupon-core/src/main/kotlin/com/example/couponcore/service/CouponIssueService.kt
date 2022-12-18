package com.example.couponcore.service

import com.example.couponcore.repository.RedisRepository
import com.example.couponcore.utils.DistributedLock
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.AopContext
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Service
class CouponIssueService(
    private val userCouponPolicyMappingService: UserCouponPolicyMappingService,
    private val couponPolicyService: CouponPolicyService,
    private val redisRepository: RedisRepository
) {
    companion object {
        fun getIssuedCouponSetKey(key: String) = "${key}_issue_set"
    }

    private val log = LoggerFactory.getLogger(this::class.simpleName)
    private val self: CouponIssueService by lazy { AopContext.currentProxy() as CouponIssueService }

    fun issue(key: String, userId: String) {
        val issuable = issuableCouponDate(key) && self.markIssueStatus(key, userId)
        if (issuable) syncCouponIssueStatus(key, userId)
    }

    @DistributedLock(lockName = "issue_lock", waitTime = 3000, leaseTime = 3000, unit = TimeUnit.MILLISECONDS)
    fun markIssueStatus(key: String, userId: String): Boolean {
        if (issuableCouponQuantity(key).not()) return false
        val isSuccessIssueTrueMarking = addIssueMarking(getIssuedCouponSetKey(key), userId)
        if (isSuccessIssueTrueMarking.not()) {
            log.info("key : ${getIssuedCouponSetKey(key)} , userId: $userId 발급 마킹 실패 (중복 요청)")
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

    fun issuableCouponDate(title: String): Boolean {
        val couponPolicyDto = couponPolicyService.findCouponPolicy(title)
        val now = OffsetDateTime.now()
        val issueStart = couponPolicyDto.dateIssueStart
        val issueEnd = couponPolicyDto.dateIssueEnd
        return issueStart <= now && now <= issueEnd
    }

    fun issuableCouponQuantity(key: String): Boolean {
        return getCouponIssueCount(getIssuedCouponSetKey(key)) < getTotalCouponQuantity(key)
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
