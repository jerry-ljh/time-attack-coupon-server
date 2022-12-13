package com.example.couponbatch.configuration

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

class BatchDataSourceConfiguration(dataSource: DataSource) : DefaultBatchConfigurer(dataSource)

@Configuration
class InMemoryBatchConfiguration : BeanPostProcessor {

    private class InMemoryBatchConfigurer : DefaultBatchConfigurer()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is DefaultBatchConfigurer) {
            return InMemoryBatchConfigurer()
        }
        return super.postProcessBeforeInitialization(bean, beanName)
    }
}