package com.example.couponapi.dto

import com.example.couponcore.domain.CouponCode
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class WaitingQueueRequestDto(
    val userId: String,
    val couponCode: CouponCode,
)
