package com.example.couponbatch.job

import com.example.couponbatch.CouponBatchApplication
import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.dto.CouponPolicyDto
import com.example.couponcore.repository.CouponPolicyRepository
import com.example.couponcore.repository.RedisRepository
import com.example.couponcore.service.CouponIssueService
import com.example.couponcore.service.WaitingQueueService
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
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
class CouponIssueJobReaderTest(
    private val waitingQueueService: WaitingQueueService,
    private val couponIssueService: CouponIssueService,
    private val couponPolicyRepository: CouponPolicyRepository,
    private val redisRepository: RedisRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        const val TEST_COUPON = "TEST_COUPON"
    }

    @AfterEach
    fun deleteAllKey() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }

    @Test
    fun `read 대기열에 있는 userId를 가져온다`() {
        // given
        val reader = reader(TEST_COUPON, quantity = 3)
        val userId = "jerry"
        waitingQueueService.registerQueue(TEST_COUPON, userId)
        // when
        val result = reader.read()
        // then
        result shouldBe userId
    }

    @Test
    fun `read 모든 쿠폰을 발급한 경우 null을 반환한다`() {
        // given
        val reader = reader(TEST_COUPON, quantity = 3)
        issueCoupon(TEST_COUPON, "jerry")
        issueCoupon(TEST_COUPON, "tom")
        issueCoupon(TEST_COUPON, "jack")
        waitingQueueService.registerQueue(TEST_COUPON, "con")
        // when
        val result = reader.read()
        // then
        result shouldBe null
    }

    private fun reader(title: String, quantity: Long): CouponIssueJobReader {
        val couponPolicyDto = setCouponTitle(title, quantity = quantity)
        return CouponIssueJobReader(
            waitingQueueService,
            couponIssueService,
            couponPolicyDto
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

    private fun issueCoupon(title: String, userId: String) {
        redisRepository.sAdd(CouponIssueService.getIssuedCouponSetKey(title), userId)
    }
}
