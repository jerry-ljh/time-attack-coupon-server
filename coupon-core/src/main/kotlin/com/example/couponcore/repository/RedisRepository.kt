package com.example.couponcore.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val objectMapper = jacksonObjectMapper()
    private val deserializer = redisTemplate.defaultSerializer!!

    fun zAdd(key: String, value: Any, score: Double): Boolean? {
        return redisTemplate.opsForZSet().add(key, value, score)
    }

    fun zAddIfAbsent(key: String, value: Any, score: Double): Boolean? {
        return redisTemplate.opsForZSet().addIfAbsent(key, value, score)
    }

    fun <T : Any> zRange(key: String, startRank: Long, endRank: Long, type: Class<T>): Set<T>? {
        return redisTemplate.opsForZSet().range(key, startRank, endRank)
            ?.map { type.cast(it) }
            ?.toSet()
    }

    fun <T : Any> zPopMin(key: String, count: Long, type: Class<T>): Set<T>? {
        return redisTemplate.opsForZSet().popMin(key, count)
            ?.map { type.cast(it.value) }
            ?.toSet()
    }

    fun zRank(key: String, value: Any): Long? {
        return redisTemplate.opsForZSet().rank(key, value)
    }

    fun zSize(key: String): Long? {
        return redisTemplate.opsForZSet().size(key)
    }

    fun sAdd(key: String, value: Any): Long? {
        return redisTemplate.opsForSet().add(key, value)
    }

    fun sRem(key: String, value: Any): Long? {
        return redisTemplate.opsForSet().remove(key, value)
    }

    fun sCard(key: String): Long? {
        return redisTemplate.opsForSet().size(key)
    }
}
