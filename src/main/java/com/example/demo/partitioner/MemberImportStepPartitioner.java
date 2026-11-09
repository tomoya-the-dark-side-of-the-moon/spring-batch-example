package com.example.demo.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.demo.datasource.MemberRoutingDataSource;
import com.example.demo.domain.Reservation;

public class MemberImportStepPartitioner implements Partitioner {

	private final List<Reservation> reservations;

	private final MemberRoutingDataSource memberRoutingDataSource;

	public MemberImportStepPartitioner(@Value("#{jobExecutionContext['reservations']}") List<Reservation> reservations,
			MemberRoutingDataSource memberRoutingDataSource) {
		this.reservations = reservations;
		this.memberRoutingDataSource = memberRoutingDataSource;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>();
		int i = 0;
		for (Reservation reservation : this.reservations) {
			ExecutionContext executionContext = new ExecutionContext();
			executionContext.put("fileName", reservation.fileName());
			executionContext.put("id", reservation.id());
			map.put("reservation-" + i, executionContext);

			String url = String.format("jdbc:postgresql://%s:5432/%s", reservation.host(), reservation.dbName());
			String username = reservation.userName();
			String password = reservation.password();
			DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
			dataSource.setDriverClassName("org.postgresql.Driver");
			this.memberRoutingDataSource.addDataSource(reservation.id(), dataSource);

			i++;
		}

		return map;
	}

}
