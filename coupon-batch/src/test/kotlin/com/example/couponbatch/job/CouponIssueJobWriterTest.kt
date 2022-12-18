package com.example.couponbatch.job

import com.example.couponbatch.CouponBatchApplication
import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.repository.CouponPolicyRepository
import com.example.couponcore.service.CouponIssueService
import io.kotest.matchers.shouldBe
import io.mockk.Ordering
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@TestPropertySource(properties = ["spring.config.name=application-batch,application-core"])
@SpringBootTest(classes = [CouponBatchApplication::class])
class CouponIssueJobWriterTest(
    couponIssueService: CouponIssueService,
    private val couponPolicyRepository: CouponPolicyRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        const val TEST_COUPON = "TEST_COUPON"
    }

    private val couponIssueService = spyk(couponIssueService)

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }

    @Test
    fun `write 발급 대상을 모두 발급해도 쿠폰 발급 수 제한에 걸리지 않으면 async로 발급한다`() {
        // given
        val sut = writer(title = TEST_COUPON, quantity = 3)
        val userIdList = mutableListOf("jerry", "tom", "jack")
        // when
        sut.write(userIdList)
        // then
        verify {
            couponIssueService.issue(key = TEST_COUPON, userId = "jerry")
            couponIssueService.issue(key = TEST_COUPON, userId = "tom")
            couponIssueService.issue(key = TEST_COUPON, userId = "jack")
        }
        couponIssueService.getCouponIssueCount(key = getIssueKey(TEST_COUPON)) shouldBe 3
    }

    @Test
    fun `write 발급 대상을 모두 발급하면 쿠폰 발급 수 제한에 걸리는 경우 sync로 순차발급한다`() {
        // given
        val sut = writer(title = TEST_COUPON, quantity = 2)
        val userIdList = mutableListOf("jerry", "tom", "jack")
        // when
        sut.write(userIdList)
        // then
        verify(Ordering.ORDERED) {
            couponIssueService.issue(key = TEST_COUPON, userId = "jerry")
            couponIssueService.issue(key = TEST_COUPON, userId = "tom")
            couponIssueService.issue(key = TEST_COUPON, userId = "jack")
        }
        couponIssueService.getCouponIssueCount(key = getIssueKey(TEST_COUPON)) shouldBe 2
    }

    private fun writer(title: String, quantity: Long): CouponIssueJobWriter {
        val couponPolicyDto = setCouponTitle(title, quantity = quantity)
        return CouponIssueJobWriter(
            couponIssueService,
            couponPolicyDto,
            ThreadPoolTaskExecutor().apply {
                corePoolSize = 10
                initialize()
            }
        )
    }

    private fun setCouponTitle(title: String, quantity: Long): CouponPolicyDto {
        couponPolicyRepository.save(
            CouponPolicy(
                title = title,
                quantity = quantity,
                issuedQuantity = 0,
                dateIssueStart = OffsetDateTime.now(),
                dateIssueEnd = OffsetDateTime.now().plusDays(3),
                dateExpire = OffsetDateTime.now().plusDays(3),
            )
        )
        return couponPolicyRepository.findCouponPolicyDto(title)
    }

    private fun getIssueKey(key: String) = CouponIssueService.getIssuedCouponSetKey(key)
}
