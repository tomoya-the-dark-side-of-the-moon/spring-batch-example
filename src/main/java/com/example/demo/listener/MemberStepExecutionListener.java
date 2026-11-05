package com.example.demo.listener;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemberStepExecutionListener implements StepExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(MemberStepExecutionListener.class);

	private final JdbcOperations jdbcOperations;

	public MemberStepExecutionListener(DataSource dataSource) {
		this.jdbcOperations = new JdbcTemplate(dataSource);
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		countLogging();
	}

	@Override
	public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
		countLogging();

		return ExitStatus.COMPLETED;
	}

	private void countLogging() {
		String sql = "SELECT count(*) FROM member";
		int count = this.jdbcOperations.queryForObject(sql, Integer.class);

		logger.info("count of member: {}", count);
	}

}
