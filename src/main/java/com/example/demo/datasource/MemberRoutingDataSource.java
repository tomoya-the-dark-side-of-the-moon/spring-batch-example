package com.example.demo.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MemberRoutingDataSource extends AbstractRoutingDataSource {

	private final Map<Integer, DataSource> dataSources = new HashMap<Integer, DataSource>();

	@Override
	public void initialize() {
		// no op
	}

	@Override
	protected @Nullable Object determineCurrentLookupKey() {
		return DataSourceKey.getDataSourceKey();
	}

	public void add_DataSource(Integer key, DataSource dataSource) {
		this.dataSources.put(key, dataSource);
	}

}
