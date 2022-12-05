package com.example.couponapi

import com.example.couponcore.CoreConfigurationLoader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(CoreConfigurationLoader::class)
@SpringBootApplication
class CouponApiApplication

fun main(args: Array<String>) {
    runApplication<CouponApiApplication>(*args)
}
