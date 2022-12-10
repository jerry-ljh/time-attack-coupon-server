package com.example.couponbatch.job

import com.example.couponcore.domain.User
import com.example.couponcore.service.UserService
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@ConditionalOnProperty(name = ["spring.batch.job.names"], havingValue = "userInsertJob")
class UserInsertJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val userService: UserService
) {

    @Bean
    fun userInsertJob(userInsertStep: Step): Job {
        return jobBuilderFactory["userInsertJob"]
            .start(userInsertStep)
            .build()
    }

    @Bean
    @JobScope
    fun userInsertStep(
        @Value("#{jobParameters[count]}") count: Long,
    ): Step {
        return stepBuilderFactory["userInsertStep"]
            .tasklet { _, _ ->
                val userList = (1..count).map { User(userId = UUID.randomUUID().toString()) }
                userService.saveAllUser(userList)
                RepeatStatus.FINISHED
            }
            .build()
    }

}