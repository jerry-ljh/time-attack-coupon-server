package com.example.couponcore.config

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
class DatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun hikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean
    @Profile("local")
    fun localDataSource(hikariConfig: HikariConfig): DataSource {
        return HikariDataSource(hikariConfig)
    }

    @Bean
    @Profile("prod")
    fun createDataSource(ssmClient: AWSSimpleSystemsManagement, hikariConfig: HikariConfig): DataSource {
        hikariConfig.jdbcUrl = getParameter(ssmClient, hikariConfig.jdbcUrl)
        hikariConfig.username = getParameter(ssmClient, hikariConfig.username)
        hikariConfig.password = getParameter(ssmClient, hikariConfig.password)
        return HikariDataSource(hikariConfig)
    }

    fun getParameter(ssmClient: AWSSimpleSystemsManagement, parameterStoreName: String): String? {
        val request = GetParameterRequest().withName(parameterStoreName).withWithDecryption(true)
        return ssmClient.getParameter(request).parameter.value
    }
}
