package com.example.couponcore.domain

import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "user_coupon_policy_mappings", schema = "coupon")
class UserCouponPolicyMapping(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY) val couponPolicy: CouponPolicy,
    @Column(nullable = false) val userId: String,
    @Column(nullable = false) val dateIssued: OffsetDateTime,
    @Column(nullable = false) val dateExpire: OffsetDateTime,
    @Column val dateUsed: OffsetDateTime? = null,
)
