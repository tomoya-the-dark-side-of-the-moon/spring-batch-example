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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.FullNameMember;
import com.example.demo.domain.Member;
import com.example.demo.listener.MemberItemSkipListener;
import com.example.demo.listener.MemberStepExecutionListener;

@Configuration
@EnableJdbcJobRepository
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
	ItemWriter<FullNameMember> itemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<FullNameMember>().dataSource(dataSource).sql(
				"INSERT INTO member (id, first_name, last_name, full_name) VALUES (:id, :firstName, :lastName, :fullName)")
				.beanMapped().build();
	}

	@Bean
	SkipListener<Member, FullNameMember> skipListener() {
		return new MemberItemSkipListener();
	}

	@Bean
	StepExecutionListener stepExecutionListener(DataSource dataSource) {
		return new MemberStepExecutionListener(dataSource);
	}

	@Bean
	Step step(JobRepository jobRepository, ItemReader<Member> itemReader,
			ItemProcessor<Member, FullNameMember> itemProcessor, ItemWriter<FullNameMember> itemWriter,
			StepExecutionListener stepExecutionListener, SkipListener<Member, FullNameMember> skipListener) {
		return new StepBuilder(jobRepository).<Member, FullNameMember>chunk(5).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter).skip(ValidationException.class).skipListener(skipListener)
				.faultTolerant().listener(stepExecutionListener).build();
	}

	@Bean
	Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder(jobRepository).start(step).build();
	}

}
