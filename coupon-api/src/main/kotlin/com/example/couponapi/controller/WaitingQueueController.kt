package com.example.couponapi.controller

import com.example.couponapi.dto.WaitingQueueRequestDto
import com.example.couponapi.service.WaitingQueueApiService
import org.springframework.web.bind.annotation.*

@RestController
class WaitingQueueController(
    private val waitingQueueService: WaitingQueueApiService,
) {

    @PostMapping("/queue")
    fun registerQueue(@RequestBody waitingQueueRequestDto: WaitingQueueRequestDto): Boolean {
        return waitingQueueService.register(waitingQueueRequestDto)
    }

    @GetMapping("/queue")
    fun getWaitingOrder(
        @RequestParam(name = "user_id") userId: String,
        @RequestParam(name = "coupon_title") couponTitle: String
    ): Long? {
        return waitingQueueService.getWaitingOrder(couponTitle = couponTitle, userId = userId)
    }
}
