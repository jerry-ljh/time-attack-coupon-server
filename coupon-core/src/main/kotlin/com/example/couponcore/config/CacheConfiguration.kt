package com.example.couponcore.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class CacheConfiguration(private val redisConnectionFactory: RedisConnectionFactory) {

    @Bean
    fun cacheManager(): CacheManager {
        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(defaultConfig())
            .build()
    }

    private fun defaultConfig(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(
                        jacksonObjectMapper()
                            .registerModule(JavaTimeModule())
                            .activateDefaultTyping(
                                BasicPolymorphicTypeValidator.builder()
                                    .allowIfBaseType(Any::class.java)
                                    .build(), ObjectMapper.DefaultTyping.EVERYTHING
                            )

                    )
                )
            )

    }
}