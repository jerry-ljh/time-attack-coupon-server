package com.example.couponbatch.job

import com.example.couponbatch.BatchTestConfig
import com.example.couponcore.domain.CouponPolicy
import com.example.couponcore.repository.CouponPolicyJpaRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.test.context.TestPropertySource
import java.time.OffsetDateTime

@TestPropertySource(properties = ["spring.batch.job.names=couponIssueJob"])
class CouponIssueJobConfigurationTest(
    private val couponPolicyJpaRepository: CouponPolicyJpaRepository
) : BatchTestConfig() {

    @BeforeEach
    fun setCouponPolicy() {
        couponPolicyJpaRepository.save(
            CouponPolicy(
                title = "JERRY_SALE_COUPON",
                quantity = 999,
                issuedQuantity = 0,
                dateIssueStart = OffsetDateTime.now(),
                dateIssueEnd = OffsetDateTime.now().plusDays(3),
                dateExpire = OffsetDateTime.now().plusDays(3),
            )
        )
    }

    @Test
    fun `run`() {
        // given
        val jobParameters = JobParametersBuilder()
            .addLong("version", System.currentTimeMillis())
            .addString("couponTitle", "JERRY_SALE_COUPON")
            .addLong("threadCount", 10)
            .toJobParameters()
        // when
        val result = jobLauncherTestUtils.launchJob(jobParameters)
        // then
        result.status shouldBe BatchStatus.COMPLETED
    }
}
