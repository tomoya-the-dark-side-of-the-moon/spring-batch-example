package com.example.demo.conf;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.domain.FullNameMember;
import com.example.demo.domain.Member;
import com.example.demo.listener.MemberItemSkipListener;
import com.example.demo.listener.MemberStepExecutionListener;

@Configuration
@EnableJdbcJobRepository
@Import(value = { DataSourceConfiguration.class })
public class BatchConfiguration {

	@Bean
	ItemReader<Member> itemReader() {
		return new FlatFileItemReaderBuilder<Member>().name("itemReader")
				.resource(new ClassPathResource("bon_jovi.csv")).delimited().names("id", "firstName", "lastName")
				.linesToSkip(1).targetType(Member.class).build();
	}

	@Bean
	ItemProcessor<Member, Member> beanValidatingItemProcessor() throws Exception {
		BeanValidatingItemProcessor<Member> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
		beanValidatingItemProcessor.setFilter(false);
		beanValidatingItemProcessor.afterPropertiesSet();

		return beanValidatingItemProcessor;
	}

	@Bean
	ItemProcessor<Member, FullNameMember> itemProcessor() throws Exception {
		ItemProcessor<Member, FullNameMember> fullNameItemProcessor = item -> new FullNameMember(item.getId(),
				item.getFirstName(), item.getLastName(), item.getFirstName() + " " + item.getLastName());

		return new CompositeItemProcessor<Member, FullNameMember>(
				List.of(beanValidatingItemProcessor(), fullNameItemProcessor));
	}

	@Bean
	ItemWriter<FullNameMember> itemWriter(@Qualifier("businessDataSource") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<FullNameMember>().dataSource(dataSource).sql("""
				INSERT INTO member (id, first_name, last_name, full_name)
				VALUES (CAST(:id AS INTEGER), :firstName, :lastName, :fullName)
				ON CONFLICT (id) DO UPDATE SET
					first_name = EXCLUDED.first_name,
					last_name = EXCLUDED.last_name,
					full_name = EXCLUDED.full_name
				""").beanMapped().build();
	}

	@Bean
	SkipListener<Member, FullNameMember> skipListener() {
		return new MemberItemSkipListener();
	}

	@Bean
	StepExecutionListener stepExecutionListener(@Qualifier("businessDataSource") DataSource dataSource) {
		return new MemberStepExecutionListener(dataSource);
	}

	@Bean
	Step step(JobRepository jobRepository, ItemReader<Member> itemReader,
			ItemProcessor<Member, FullNameMember> itemProcessor, ItemWriter<FullNameMember> itemWriter,
			StepExecutionListener stepExecutionListener, SkipListener<Member, FullNameMember> skipListener,
			@Qualifier("businessPlatformTransactionManager") PlatformTransactionManager businessPlatbusinessPlatformTransactionManager) {
		return new StepBuilder(jobRepository).<Member, FullNameMember>chunk(5).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter).skip(ValidationException.class).skipListener(skipListener)
				.faultTolerant().listener(stepExecutionListener)
				.transactionManager(businessPlatbusinessPlatformTransactionManager).build();
	}

	@Bean
	Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder(jobRepository).start(step).build();
	}

}
