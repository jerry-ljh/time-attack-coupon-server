package com.example.couponcore.utils

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val lockName: String,
    val waitTime: Long,
    val leaseTime: Long,
    val unit: TimeUnit = TimeUnit.MILLISECONDS
)