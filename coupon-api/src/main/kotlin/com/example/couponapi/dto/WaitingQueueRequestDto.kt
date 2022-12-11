package com.example.couponapi.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class WaitingQueueRequestDto(
    val userId: String,
    val couponTitle: String,
)
