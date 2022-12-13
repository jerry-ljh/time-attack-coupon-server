package com.example.couponapi.controller

import com.example.couponapi.dto.WaitingQueueRequestDto
import com.example.couponcore.service.WaitingQueueService
import org.springframework.web.bind.annotation.*

@RestController
class WaitingQueueController(
    private val waitingQueueService: WaitingQueueService
) {

    @PostMapping("/queue")
    fun registerQueue(@RequestBody waitingQueueRequestDto: WaitingQueueRequestDto): Boolean {
        val result = waitingQueueService.registerQueue(
            key = waitingQueueRequestDto.couponTitle,
            value = waitingQueueRequestDto.userId
        )
        return result ?: false
    }

    @GetMapping("/queue")
    fun getWaitingOrder(
        @RequestParam(name = "user_id") userId: String,
        @RequestParam(name = "coupon_title") couponTitle: String
    ): Long? {
        return waitingQueueService.getWaitingOrder(key = couponTitle, value = userId)
    }
}
