package com.example.couponcore

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = ["spring.config.name=application-domain-test"])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@SpringBootTest(classes = [CoreConfigurationLoader::class])
class TestConfig