package com.example.couponcore.config

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class AwsSsmConfiguration {

    @Bean
    fun ssmClient(): AWSSimpleSystemsManagement {
        return AWSSimpleSystemsManagementClientBuilder.standard()
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .withRegion(Regions.AP_NORTHEAST_2).build()
    }
}
