package com.example.couponbatch.job

import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.service.WaitingQueueService
import org.springframework.batch.item.ItemReader

class CouponIssueJobReader(
    private val waitingQueueService: WaitingQueueService,
    private val couponPolicyDto: CouponPolicyDto
) : ItemReader<String> {

    override fun read(): String? {
        return waitingQueueService.popQueue(couponPolicyDto.title, count = 1, String::class.java).firstOrNull()
    }
}
