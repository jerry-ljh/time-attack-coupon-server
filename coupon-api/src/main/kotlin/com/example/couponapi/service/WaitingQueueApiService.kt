package com.example.couponapi.service

import com.example.couponapi.dto.WaitingQueueRequestDto
import com.example.couponcore.service.CouponPolicyService
import com.example.couponcore.service.WaitingQueueService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WaitingQueueApiService(
    private val waitingQueueService: WaitingQueueService,
    private val couponPolicyService: CouponPolicyService
) {

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    fun register(input: WaitingQueueRequestDto): Boolean {
        if (couponPolicyService.isIssuableDate(input.couponTitle).not()) {
            log.info("${input.couponTitle} 쿠폰 발급 기간이 아닙니다.")
            return false
        }
        val result = waitingQueueService.registerQueue(
            key = input.couponTitle,
            value = input.userId
        )
        return result ?: false
    }

    fun getWaitingOrder(couponTitle: String, userId: String): Long? {
        return waitingQueueService.getWaitingOrder(key = couponTitle, value = userId)
    }
}
