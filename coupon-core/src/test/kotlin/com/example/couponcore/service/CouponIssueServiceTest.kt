package com.example.couponcore.service

import com.example.couponcore.TestConfig
import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.repository.CouponPolicyRepository
import com.example.couponcore.repository.RedisRepository
import com.example.couponcore.repository.UserCouponPolicyMappingRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Transactional
class CouponIssueServiceTest(
    private val couponIssueService: CouponIssueService,
    private val couponPolicyRepository: CouponPolicyRepository,
    private val userCouponPolicyMappingRepository: UserCouponPolicyMappingRepository,
    private val redisRepository: RedisRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) : TestConfig() {

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }

    @Test
    fun `markIssueStatus 쿠폰 발급 완료 상태로 마킹한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        saveCouponPolicy(title = key, quantity = 100)
        // when
        val result = couponIssueService.markIssueStatus(key, userId)
        // then
        result shouldBe true
        val issuedCouponUserSet = redisTemplate.opsForSet().members(CouponIssueService.getIssuedCouponSetKey(key))!!
        issuedCouponUserSet.first() shouldBe userId
    }

    @Test
    fun `markIssueStatus 발급 가능한 쿠폰 수가 없다면 false를 반환한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        val issuedCouponSetKey = CouponIssueService.getIssuedCouponSetKey(key)
        saveCouponPolicy(title = key, quantity = 3)
        redisRepository.sAdd(issuedCouponSetKey, "eliot")
        redisRepository.sAdd(issuedCouponSetKey, "ben")
        redisRepository.sAdd(issuedCouponSetKey, "con")
        // when
        val result = couponIssueService.markIssueStatus(key, userId)
        // then
        result shouldBe false
    }

    @Test
    fun `markIssueStatus 유저가 중복 발급 요청을 하면 false를 반환한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        val issuedCouponSetKey = CouponIssueService.getIssuedCouponSetKey(key)
        saveCouponPolicy(title = key, quantity = 3)
        redisRepository.sAdd(issuedCouponSetKey, "jerry")
        // when
        val result = couponIssueService.markIssueStatus(key, userId)
        // then
        result shouldBe false
    }

    @Test
    fun `rollbackIssueMarking 발급 완료 set에 저장된 user를 제거한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        redisRepository.sAdd(key, userId)
        // when
        val result = couponIssueService.rollbackIssueMarking(key, userId)
        // then
        result shouldBe true
        val issuedCouponUserSet = redisTemplate.opsForSet().members(key)!!
        issuedCouponUserSet.find { it == userId } shouldBe null
    }

    @Test
    fun `addIssueMarking 발급 마킹시 발급 완료 Set에 저장한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        // when
        val result = couponIssueService.addIssueMarking(key, userId)
        // then
        result shouldBe true
        val issuedCouponUserSet = redisTemplate.opsForSet().members(key)!!
        issuedCouponUserSet shouldHaveSize 1
        issuedCouponUserSet.first() shouldBe userId
    }

    @Test
    fun `addIssueMarking 중복된 유저 마킹시 false를 반환한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        // when
        val firstResult = couponIssueService.addIssueMarking(key, userId)
        val secondResult = couponIssueService.addIssueMarking(key, userId)
        // then
        firstResult shouldBe true
        secondResult shouldBe false
    }

    @Test
    fun `getCouponIssueCount 발급된 쿠폰 수량을 확인한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        redisRepository.sAdd(key, "Jerry")
        redisRepository.sAdd(key, "tom")
        redisRepository.sAdd(key, "tony")
        // when
        val result = couponIssueService.getCouponIssueCount(key)
        // then
        result shouldBe 3
    }

    @Test
    fun `getTotalCouponQuantity 쿠폰의 총 발행 가능한 수량을 가져온다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val couponPolicy = saveCouponPolicy(title = key, quantity = 10000)
        // when
        val result = couponIssueService.getTotalCouponQuantity(key)
        // then
        result shouldBe couponPolicy.quantity
    }

    @Test
    fun `syncCouponIssueStatus DB에 쿠폰 발급 처리를 한다`() {
        // given
        val key = "TEST_COUPON_TITLE"
        val userId = "jerry"
        val couponPolicy = saveCouponPolicy(title = key, quantity = 10000)
        // when
        couponIssueService.syncCouponIssueStatus(key, userId)
        // then
        val userCouponMappingList = userCouponPolicyMappingRepository.findAll()
        val mapping = userCouponMappingList.find { it.couponPolicy == couponPolicy && it.userId == userId }
        mapping shouldNotBe null
    }

    @Test
    fun `syncCouponIssueStatus 쿠폰 발급에 실패하면 redis 발급 완료 마킹을 롤백한다`() {
        // given
        val key = "NOT_EXIST_COUPON"
        val userId = "jerry"
        redisRepository.sAdd(key, userId)
        // when
        couponIssueService.syncCouponIssueStatus(key, userId)
        // then
        val issuedCouponUserSet = redisTemplate.opsForSet().members(CouponIssueService.getIssuedCouponSetKey(key))!!
        issuedCouponUserSet.find { it == userId } shouldBe null
    }

    private fun saveCouponPolicy(title: String, quantity: Long): CouponPolicy {
        return couponPolicyRepository.save(
            CouponPolicy(
                title = title,
                quantity = quantity,
                issuedQuantity = 0,
                dateIssueStart = OffsetDateTime.now(),
                dateIssueEnd = OffsetDateTime.now().plusDays(5),
                dateExpire = OffsetDateTime.now().plusDays(5)
            )
        )
    }
}
