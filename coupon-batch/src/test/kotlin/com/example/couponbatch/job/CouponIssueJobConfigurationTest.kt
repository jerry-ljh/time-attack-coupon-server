package com.example.couponbatch.job

import com.example.couponbatch.BatchTestConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = ["spring.batch.job.names=couponIssueJob"])
class CouponIssueJobConfigurationTest : BatchTestConfig() {

    @Test
    fun `run`() {
        // given
        val jobParameters = JobParametersBuilder()
            .addLong("version", System.currentTimeMillis())
            .addString("couponTitle", "TIME_SALE")
            .toJobParameters()
        // when
        val result = jobLauncherTestUtils.launchJob(jobParameters)
        // then
        result.status shouldBe BatchStatus.COMPLETED
    }
}
