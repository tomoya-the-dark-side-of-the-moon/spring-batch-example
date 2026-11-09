package com.example.demo.listener;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;

import com.example.demo.datasource.DataSourceKey;

public class MemberImportStepExecutionListener implements StepExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(MemberImportStepExecutionListener.class);

	public MemberImportStepExecutionListener() {
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();

		Integer id = executionContext.getInt("id");
		String fileName = executionContext.getString("fileName");

		logger.info("id: {}, fileName: {}", id, fileName);

		DataSourceKey.setDataSourceKey(id);
	}

	@Override
	public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
		DataSourceKey.clearDataSourceKey();

		return stepExecution.getExitStatus();
	}

}
