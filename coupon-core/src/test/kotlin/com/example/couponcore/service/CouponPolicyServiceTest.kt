package com.example.couponcore.service

import com.example.couponcore.TestConfig
import org.junit.jupiter.api.AfterEach
import org.springframework.data.redis.core.RedisTemplate

class CouponPolicyServiceTest(
    private val couponPolicyService: CouponPolicyService,
    private val redisTemplate: RedisTemplate<String, Any>
) : TestConfig() {

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }
}
