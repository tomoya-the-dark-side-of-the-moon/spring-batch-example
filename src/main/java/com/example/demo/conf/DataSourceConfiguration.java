package com.example.demo.conf;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.batch.jdbc.autoconfigure.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.datasource.MemberRoutingDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DataSourceConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	DataSourceProperties batchDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean("batchDataSource")
	@Primary
	@BatchDataSource
	DataSource batchDataSource() {
		return batchDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean("batchTransactionManager")
	@Primary
	PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource") DataSource batchDataSource) {
		return new DataSourceTransactionManager(batchDataSource);
	}

	@Bean("memberRoutingDataSource")
	MemberRoutingDataSource memberRoutingDataSource() {
		return new MemberRoutingDataSource();
	}

	@Bean("memberImportDataSource")
	@StepScope
	DataSource businessDataSource() {
		return memberRoutingDataSource().determineTargetDataSource();
	}

	@Bean("memberImportEntityManagerFactory")
	LocalEntityManagerFactoryBean memberImportEntityManagerFactory(
			@Qualifier("memberImportDataSource") DataSource memberImportDataSource) {
		LocalEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		localEntityManagerFactoryBean.setPackagesToScan("com.example.demo.domain");
		localEntityManagerFactoryBean.setDataSource(memberImportDataSource);
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		localEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		localEntityManagerFactoryBean.setJpaPropertyMap(
				Map.of("hibernate.boot.allow_jdbc_metadata_access", "false", "hibernate.hbm2ddl.auto", "none"));

		return localEntityManagerFactoryBean;
	}

	@Bean("memberImportTransactionManager")
	PlatformTransactionManager memberImportTransactionManager(
			@Qualifier("memberImportEntityManagerFactory") EntityManagerFactory memberImportEntityManagerFactory) {
		return new JpaTransactionManager(memberImportEntityManagerFactory);
	}

}
