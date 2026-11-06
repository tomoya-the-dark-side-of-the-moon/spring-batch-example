package com.example.demo.conf;

import javax.sql.DataSource;

import org.springframework.boot.batch.jdbc.autoconfigure.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	DataSourceProperties batchDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@BatchDataSource
	DataSource batchDataSource() {
		return batchDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean
	@ConfigurationProperties("spring.business.datasource")
	DataSourceProperties businessDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean("businessDataSource")
	DataSource businessDataSource() {
		return businessDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean("businessPlatformTransactionManager")
	PlatformTransactionManager businessPlatformTransactionManager() {
		return new DataSourceTransactionManager(businessDataSource());
	}

}
