package com.example.couponbatch.job

import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.service.CouponIssueService
import com.example.couponcore.service.WaitingQueueService
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader

class CouponIssueJobReader(
    private val waitingQueueService: WaitingQueueService,
    private val couponIssueService: CouponIssueService,
    private val couponPolicyDto: CouponPolicyDto
) : ItemReader<String> {

    private val log = LoggerFactory.getLogger(this::class.simpleName)
    private val totalCouponQuantity = couponIssueService.getTotalCouponQuantity(couponPolicyDto.title)
    private val issuedCouponSetKey = CouponIssueService.getIssuedCouponSetKey(couponPolicyDto.title)

    override fun read(): String? {
        while (issuableCouponQuantity()) {
            val userId = getUserId()
            if (userId != null) return userId
            log.info("발급 대기열이 비어있습니다. COUPON_TITLE: ${couponPolicyDto.title}")
            Thread.sleep(3000)
        }
        return null
    }

    private fun getUserId(): String? {
        return waitingQueueService.popQueue(couponPolicyDto.title, count = 1, String::class.java).firstOrNull()
    }

    private fun issuableCouponQuantity(): Boolean {
        val issuedCouponCount = couponIssueService.getCouponIssueCount(issuedCouponSetKey)
        return issuedCouponCount < totalCouponQuantity
    }
}
