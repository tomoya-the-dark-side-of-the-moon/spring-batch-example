package com.example.demo.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.domain.Reservation;

public class MemberImportStepPartitioner implements Partitioner {

	private final List<Reservation> reservations;

	public MemberImportStepPartitioner(
			@Value("#{jobExecutionContext['reservations']}") List<Reservation> reservations) {
		this.reservations = reservations;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>();
		// TODO Auto-generated method stub
		return null;
	}

}
