package com.example.couponapi.service

import com.example.couponapi.dto.WaitingQueueRequestDto
import com.example.couponcore.service.CouponIssueService
import com.example.couponcore.service.WaitingQueueService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WaitingQueueApiService(
    private val waitingQueueService: WaitingQueueService,
    private val couponIssueService: CouponIssueService
) {

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    fun register(input: WaitingQueueRequestDto): Boolean {
        if (canRegister(input.couponTitle).not()) return false
        val result = waitingQueueService.registerQueue(
            key = input.couponTitle,
            value = input.userId
        )
        return result ?: false
    }

    fun getWaitingOrder(couponTitle: String, userId: String): Long? {
        return waitingQueueService.getWaitingOrder(key = couponTitle, value = userId)
    }

    private fun canRegister(couponTitle: String): Boolean {
        if (couponIssueService.issuableCouponDate(couponTitle).not()) {
            log.info("$couponTitle 쿠폰 발급 기간이 아닙니다.")
            return false
        }
        if (couponIssueService.issuableCouponQuantity(couponTitle).not()) {
            log.info("$couponTitle 쿠폰이 모두 발급되었습니다.")
            return false
        }
        return true
    }
}
