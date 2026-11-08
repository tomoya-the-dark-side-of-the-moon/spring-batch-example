package com.example.demo.datasource;

public class DataSourceKey {

	private static final ThreadLocal<Integer> DATA_SOURCE_KEY = new ThreadLocal<Integer>();

	public static void setDataSourceKey(Integer key) {
		DATA_SOURCE_KEY.set(key);
	}

	public static Integer getDataSourceKey() {
		return DATA_SOURCE_KEY.get();
	}

	public static void clearDataSourceKey() {
		DATA_SOURCE_KEY.remove();
	}

}
