package com.example.couponbatch.configuration

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class BatchDataSourceConfiguration(dataSource: DataSource) : DefaultBatchConfigurer(dataSource)
