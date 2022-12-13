package com.example.couponcore.utils

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributedLockAspect(
    private val redissonClient: RedissonClient
) {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @Around("@annotation(DistributedLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val distributedLock = (joinPoint.signature as MethodSignature).method.getAnnotation(DistributedLock::class.java)
        log.debug("${(joinPoint.signature as MethodSignature).method.name} 에서 LOCK(${distributedLock.lockName}) 획득 시도")
        val lock: RLock = redissonClient.getLock(distributedLock.lockName)
        val isLocked = lock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.unit)
        return try {
            if (isLocked.not()) throw IllegalStateException("[${distributedLock.lockName}] lock 획득 실패")
            log.debug("${(joinPoint.signature as MethodSignature).method.name} 에서 LOCK(${distributedLock.lockName}) 획득")
            joinPoint.proceed()
        } finally {
            if (lock.isLocked && lock.isHeldByCurrentThread) lock.unlock()
            log.debug("${(joinPoint.signature as MethodSignature).method.name} 에서 LOCK(${distributedLock.lockName}) 반납")
        }
    }
}
