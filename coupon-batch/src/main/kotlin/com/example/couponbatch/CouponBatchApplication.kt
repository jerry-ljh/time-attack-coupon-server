package com.example.couponbatch

import com.example.couponcore.CoreConfigurationLoader
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(CoreConfigurationLoader::class)
@EnableBatchProcessing
@SpringBootApplication
class CouponBatchApplication

fun main(args: Array<String>) {
    runApplication<CouponBatchApplication>(*args)
}
