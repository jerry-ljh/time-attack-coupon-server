package com.example.couponbatch.job

import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.service.CouponIssueService
import org.springframework.batch.item.ItemWriter

class CouponIssueJobWriter(
    private val couponIssueService: CouponIssueService,
    private val couponPolicyDto: CouponPolicyDto
) : ItemWriter<String> {

    override fun write(userIdList: MutableList<out String>) {
        userIdList.forEach { userId ->
            couponIssueService.issue(userId, couponPolicyDto.title)
        }
    }
}
