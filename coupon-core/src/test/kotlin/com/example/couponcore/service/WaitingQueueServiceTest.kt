package com.example.couponcore.service

import com.example.couponcore.CoreConfigurationLoader
import com.example.couponcore.repository.RedisRepository
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
class WaitingQueueServiceTest(
    private val waitingQueueService: WaitingQueueService,
    private val redisRepository: RedisRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }

    @Test
    fun `add 요청 순서에 따라서 대기열에 추가한다`() {
        // given
        val key = "test-queue"
        val value1 = TestUserDto("jerry")
        val value2 = TestUserDto("tom")
        val value3 = TestUserDto("may")
        val value4 = TestUserDto("lon")
        val value5 = TestUserDto("blue")

        // when
        waitingQueueService.registerQueue(key, value1)
        waitingQueueService.registerQueue(key, value2)
        waitingQueueService.registerQueue(key, value3)
        waitingQueueService.registerQueue(key, value4)
        waitingQueueService.registerQueue(key, value5)

        // then
        redisRepository.zSize(key) shouldBe 5
        redisRepository.zRank(key, value1) shouldBe 0
        redisRepository.zRank(key, value2) shouldBe 1
        redisRepository.zRank(key, value3) shouldBe 2
        redisRepository.zRank(key, value4) shouldBe 3
        redisRepository.zRank(key, value5) shouldBe 4
    }

    @Test
    fun `add 중복 요청이 발생하면 가장 먼저 발생한 요청만 남긴다`() {
        // given
        val key = "test-queue"
        val value = TestUserDto("jerry")
        // when
        val firstAddResult = waitingQueueService.registerQueue(key, value)
        val secondAddResult = waitingQueueService.registerQueue(key, value)
        // then
        firstAddResult shouldBe true
        secondAddResult shouldBe false
    }

    @Test
    fun `getQueue 입력된 순서 범위의 queue를 가져온다`() {
        // given
        val key = "test-queue"
        val value1 = TestUserDto("jerry")
        val value2 = TestUserDto("tom")
        val value3 = TestUserDto("may")
        // when
        waitingQueueService.registerQueue(key, value1)
        waitingQueueService.registerQueue(key, value2)
        waitingQueueService.registerQueue(key, value3)
        // when
        val result = waitingQueueService.getQueue(key, startRank = 0, endRank = 3, type = TestUserDto::class.java)
        // then
        result.poll() shouldBe value1
        result.poll() shouldBe value2
        result.poll() shouldBe value3
    }

    data class TestUserDto(val userId: String)
}

