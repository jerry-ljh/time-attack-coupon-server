package com.example.couponcore

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableCaching
@Configuration
@ComponentScan
@EnableAutoConfiguration
class CoreConfigurationLoader
