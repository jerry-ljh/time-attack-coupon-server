package com.example.couponapi.controller

import com.example.couponapi.dto.WaitingQueueRequestDto
import com.example.couponcore.service.WaitingQueueService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
}
