package com.example.couponcore.config

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {

    @Value("\${spring.redis.host}")
    private var host: String? = null

    @Value("\${spring.redis.port}")
    private var port: Int? = null

    @Bean
    @Profile("test", "local")
    fun localRedisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host!!, port!!)
    }

    @Bean
    @Profile("test", "local")
    fun localRedissonClient(): RedissonClient {
        return Redisson.create(Config().apply { useSingleServer().address = "redis://$host:$port" })
    }

    @Bean
    @Profile("prod")
    fun prodRedisConnectionFactory(ssmClient: AWSSimpleSystemsManagement): RedisConnectionFactory {
        val prodRedisHost = getParameter(ssmClient, host!!)!!
        return LettuceConnectionFactory(prodRedisHost, port!!)
    }

    @Bean
    @Profile("prod")
    fun prodRedissonClient(ssmClient: AWSSimpleSystemsManagement): RedissonClient {
        val prodRedisHost = getParameter(ssmClient, host!!)!!
        return Redisson.create(Config().apply { useSingleServer().address = "redis://$prodRedisHost:$port" })
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        val serializer = GenericJackson2JsonRedisSerializer(
            jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Any::class.java)
                        .build(),
                    ObjectMapper.DefaultTyping.EVERYTHING
                )
        )
        redisTemplate.setConnectionFactory(redisConnectionFactory)
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = serializer
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = serializer
        return redisTemplate
    }

    fun getParameter(ssmClient: AWSSimpleSystemsManagement, parameterStoreName: String): String? {
        val request = GetParameterRequest().withName(parameterStoreName).withWithDecryption(true)
        return ssmClient.getParameter(request).parameter.value
    }
}
