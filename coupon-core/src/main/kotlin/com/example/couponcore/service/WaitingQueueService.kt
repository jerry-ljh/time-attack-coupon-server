package com.example.couponcore.service

import com.example.couponcore.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class WaitingQueueService(
    private val redisRepository: RedisRepository
) {

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    fun registerQueue(key: String, value: Any): Boolean? {
        val result = redisRepository.zAddIfAbsent(key, value, score = System.currentTimeMillis().toDouble())
        log.info("register queue key: $key, value: $value, result $result")
        return result
    }

    fun <T : Any> getQueue(key: String, startRank: Long = 0, endRank: Long, type: Class<T>): Queue<T> {
        return LinkedList(redisRepository.zRange(key, startRank, endRank, type) ?: emptySet())
    }
}
