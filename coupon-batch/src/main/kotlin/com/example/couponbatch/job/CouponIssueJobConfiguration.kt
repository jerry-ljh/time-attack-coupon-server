package com.example.couponbatch.job

import com.example.couponcore.service.CouponIssueService
import com.example.couponcore.service.CouponPolicyService
import com.example.couponcore.service.WaitingQueueService
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@ConditionalOnProperty(name = ["spring.batch.job.names"], havingValue = "couponIssueJob")
class CouponIssueJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val waitingQueueService: WaitingQueueService,
    private val couponIssueService: CouponIssueService,
    private val couponPolicyService: CouponPolicyService
) {

    @Bean
    fun couponIssueJob(couponIssueStep: Step): Job {
        return jobBuilderFactory["couponIssueJob"]
            .start(couponIssueStep)
            .build()
    }

    @Bean
    @JobScope
    fun couponIssueStep(
        @Value("#{jobParameters[couponTitle]}") couponTitle: String,
        @Value("#{jobParameters[threadCount]}") threadCount: Long
    ): Step {
        val couponPolicyDto = couponPolicyService.findCouponPolicy(couponTitle)
        return stepBuilderFactory["couponIssueStep"]
            .chunk<String, String>(threadCount.toInt())
            .reader(CouponIssueJobReader(waitingQueueService, couponIssueService, couponPolicyDto))
            .writer(CouponIssueJobWriter(couponIssueService, couponPolicyDto, taskExecutor(threadCount.toInt())))
            .build()
    }

    fun taskExecutor(corePoolSize: Int): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = corePoolSize
        executor.setThreadNamePrefix("CouponIssueJob-")
        executor.initialize()
        return executor
    }
}
