package com.example.couponcore.repository

import com.example.couponcore.CoreConfigurationLoader
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@SpringBootTest(classes = [CoreConfigurationLoader::class])
class RedisRepositoryTest(
    private val redisRepository: RedisRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val key = "zset_test_key"

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }

    @Test
    fun `zAdd 중복 value는 distinct된다 `() {
        // given
        val value = "test value"
        val score = .1
        // when
        redisRepository.zAdd(key, value, score)
        redisRepository.zAdd(key, value, score)
        // then
        redisRepository.zSize(key) shouldBe 1
    }

    @Test
    fun `zAdd value가 다르면 각각 저장된다`() {
        // given
        val value1 = "test value1"
        val value2 = "test value2"
        val score = .1
        // when
        redisRepository.zAdd(key, value1, score)
        redisRepository.zAdd(key, value2, score)
        // then
        redisRepository.zSize(key) shouldBe 2
    }

    @Test
    fun `zAdd score에 따라서 순서가 결정된다(오름차순)`() {
        // given
        val value2 = "test value2"
        val score2 = .1
        val value3 = "test value3"
        val score3 = .15
        val value1 = "test value1"
        val score1 = .2
        // when
        redisRepository.zAdd(key, value1, score1)
        redisRepository.zAdd(key, value2, score2)
        redisRepository.zAdd(key, value3, score3)
        // then
        redisRepository.zRank(key, value2) shouldBe 0
        redisRepository.zRank(key, value3) shouldBe 1
        redisRepository.zRank(key, value1) shouldBe 2
    }

    @Test
    fun `zRange 집합에서 원소의 startRank 이상 endRank 이하의 값이 출력된다`() {
        // given
        val value1 = "test value1"
        val value2 = "test value2"
        val value3 = "test value3"
        redisRepository.zAdd(key, value1, score = 0.1)
        redisRepository.zAdd(key, value2, score = 0.2)
        redisRepository.zAdd(key, value3, score = 0.3)
        // when
        val result = redisRepository.zRange(key, startRank = 1, endRank = 2)
        // then
        result shouldBe setOf(value2, value3)
    }
}
