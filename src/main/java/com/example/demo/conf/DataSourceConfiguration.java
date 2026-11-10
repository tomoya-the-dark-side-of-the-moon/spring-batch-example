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
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
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

	/**
	 * エンティティのスキャン結果を Bean として定義する。Spring AOT がビルド時にスキャンを実行し、
	 * 結果をコードとして埋め込むため、クラスパススキャンができないネイティブイメージでも動作する。
	 */
	@Bean("memberImportPersistenceManagedTypes")
	static PersistenceManagedTypes memberImportPersistenceManagedTypes(ResourceLoader resourceLoader) {
		return new PersistenceManagedTypesScanner(resourceLoader).scan("com.example.demo.domain");
	}

	@Bean("memberImportEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean memberImportEntityManagerFactory(
			@Qualifier("memberImportDataSource") DataSource memberImportDataSource,
			@Qualifier("memberImportPersistenceManagedTypes") PersistenceManagedTypes memberImportPersistenceManagedTypes) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setManagedTypes(memberImportPersistenceManagedTypes);
		entityManagerFactoryBean.setDataSource(memberImportDataSource);
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		entityManagerFactoryBean.setJpaPropertyMap(
				Map.of("hibernate.boot.allow_jdbc_metadata_access", "false", "hibernate.hbm2ddl.auto", "none"));

		return entityManagerFactoryBean;
	}

	@Bean("memberImportTransactionManager")
	PlatformTransactionManager memberImportTransactionManager(
			@Qualifier("memberImportEntityManagerFactory") EntityManagerFactory memberImportEntityManagerFactory) {
		return new JpaTransactionManager(memberImportEntityManagerFactory);
	}

}
