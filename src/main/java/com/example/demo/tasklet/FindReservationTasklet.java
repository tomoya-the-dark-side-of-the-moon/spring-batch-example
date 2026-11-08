package com.example.demo.tasklet;

import java.util.List;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.domain.Reservation;

public class FindReservationTasklet implements Tasklet {

	private final JdbcOperations jdbcOperations;

	public FindReservationTasklet(DataSource dataSource) {
		this.jdbcOperations = new JdbcTemplate(dataSource);
	}

	@Override
	public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String sql = "SELECT b.id, b.file_name, a.host, a.user_name, a.db_name, a.password FROM server a INNER JOIN reservation b ON a.id = b.id ORDER BY a.id";
		List<Reservation> reservations = this.jdbcOperations.query(sql, new DataClassRowMapper<>(Reservation.class));

		ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext();
		executionContext.put("reservations", reservations);

		return RepeatStatus.FINISHED;
	}

}
