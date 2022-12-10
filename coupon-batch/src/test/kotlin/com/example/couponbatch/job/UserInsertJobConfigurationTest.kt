package com.example.couponbatch.job

import com.example.couponbatch.BatchTestConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = ["spring.batch.job.names=userInsertJob"])
class UserInsertJobConfigurationTest : BatchTestConfig() {

    @Test
    fun `run`() {
        // given
        val jobParameters = JobParametersBuilder()
            .addString("version", "1")
            .addLong("count", 10000)
            .toJobParameters()
        // when
        val result = jobLauncherTestUtils.launchJob(jobParameters)
        // then
        result.status shouldBe BatchStatus.COMPLETED
    }

}