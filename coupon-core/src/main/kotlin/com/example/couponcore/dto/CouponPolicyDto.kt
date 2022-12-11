package com.example.couponcore.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.OffsetDateTime

data class CouponPolicyDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val quantity: Long,
    val issuedQuantity: Long,
    val dateIssueStart: OffsetDateTime,
    val dateIssueEnd: OffsetDateTime,
    val dateExpire: OffsetDateTime
)