package com.example.couponcore.service

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class DistributedLockService(
    private val redissonClient: RedissonClient
) {

    fun <T> executeWithLock(lockName: String, waitSeconds: Long, leaseSeconds: Long, action: () -> T): T {
        val lock: RLock = redissonClient.getLock(lockName)
        val isLocked = lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS)
        return try {
            if (isLocked.not()) throw IllegalStateException("[$lockName] lock 획득 실패")
            action()
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) lock.unlock()
        }
    }
}