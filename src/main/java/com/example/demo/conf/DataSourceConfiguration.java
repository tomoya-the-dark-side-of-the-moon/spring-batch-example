package com.example.demo.conf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.batch.jdbc.autoconfigure.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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

	@Bean("businessEntityManagerFactory")
	LocalEntityManagerFactoryBean businessEntityManagerFactory(
			@Qualifier("businessDataSource") DataSource businessDataSource) {
		LocalEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		localEntityManagerFactoryBean.setPackagesToScan("com.example.demo.domain");
		localEntityManagerFactoryBean.setDataSource(businessDataSource);
		localEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		return localEntityManagerFactoryBean;
	}

	@Bean("businessPlatformTransactionManager")
	PlatformTransactionManager businessPlatformTransactionManager(
			@Qualifier("businessDataSource") DataSource businessDataSource) {
		return new JpaTransactionManager(businessEntityManagerFactory(businessDataSource).getObject());
	}

}
