package com.example.couponbatch

import com.example.couponcore.CoreConfigurationLoader
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess

@Import(CoreConfigurationLoader::class)
@EnableBatchProcessing
@SpringBootApplication
class CouponBatchApplication

fun main(args: Array<String>) {
    System.setProperty("spring.config.name", "application-core,application-batch")
    val exitCode = SpringApplication.exit(runApplication<CouponBatchApplication>(*args))
    exitProcess(exitCode)
}
