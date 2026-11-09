package com.example.demo.datasource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MemberRoutingDataSource extends AbstractRoutingDataSource {

	private final ConcurrentMap<Integer, DataSource> dataSources = new ConcurrentHashMap<Integer, DataSource>();

	@Override
	public void initialize() {
		// no op
	}

	@Override
	public DataSource determineTargetDataSource() {
		Object key = this.determineCurrentLookupKey();
		DataSource dataSource = key != null ? this.dataSources.get(determineCurrentLookupKey()) : null;
		if (dataSource == null) {
			throw new IllegalStateException("datasource is not found. key = ");
		}

		return dataSource;
	}

	@Override
	protected @Nullable Object determineCurrentLookupKey() {
		return DataSourceKey.getDataSourceKey();
	}

	public void addDataSource(Integer key, DataSource dataSource) {
		this.dataSources.put(key, dataSource);
	}

}
