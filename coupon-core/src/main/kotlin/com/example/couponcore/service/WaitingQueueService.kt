package com.example.couponcore.service

import com.example.couponcore.repository.RedisRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class WaitingQueueService(
    private val redisRepository: RedisRepository
) {

    fun registerQueue(key: String, value: Any): Boolean? {
        return redisRepository.zAddIfAbsent(key, value, score = System.currentTimeMillis().toDouble())
    }

    fun <T : Any> getQueue(key: String, startRank: Long = 0, endRank: Long, type: Class<T>): Queue<T> {
        return LinkedList(redisRepository.zRange(key, startRank, endRank, type) ?: emptySet())
    }
}