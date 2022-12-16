package com.example.couponbatch

import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource

@SpringBatchTest
@SpringBootTest(
    classes = [
        TestBatchConfig::class,
        CouponBatchApplication::class
    ]

)
@TestPropertySource(properties = ["spring.config.name=application-batch,application-core"])
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BatchTestConfig {

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils
}
