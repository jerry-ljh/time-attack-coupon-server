package com.example.couponbatch.job

import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.service.CouponIssueService
import org.springframework.batch.item.ItemWriter
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.CompletableFuture

class CouponIssueJobWriter(
    private val couponIssueService: CouponIssueService,
    private val couponPolicyDto: CouponPolicyDto,
    private val taskExecutor: TaskExecutor
) : ItemWriter<String> {

    private val totalCouponQuantity = couponIssueService.getTotalCouponQuantity(couponPolicyDto.title)
    private val issuedCouponSetKey = CouponIssueService.getIssuedCouponSetKey(couponPolicyDto.title)

    override fun write(userIdList: MutableList<out String>) {
        val issuedCouponCount = couponIssueService.getCouponIssueCount(issuedCouponSetKey)
        if (totalCouponQuantity <= issuedCouponCount + userIdList.count()) {
            issueAsync(userIdList)
        }
        issueSync(userIdList)
    }

    private fun issueAsync(userIds: Collection<String>) {
        userIds.map { userId ->
            CompletableFuture.runAsync({ couponIssueService.issue(couponPolicyDto.title, userId) }, taskExecutor)
        }.map { it.join() }
    }

    private fun issueSync(userIds: Collection<String>) {
        userIds.forEach { userId ->
            couponIssueService.issue(couponPolicyDto.title, userId)
        }
    }
}
