package com.example.demo.conf;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.domain.FullNameMember;
import com.example.demo.domain.Member;
import com.example.demo.domain.Reservation;
import com.example.demo.listener.MemberItemSkipListener;
import com.example.demo.listener.MemberStepExecutionListener;
import com.example.demo.partitioner.MemberImportStepPartitioner;
import com.example.demo.tasklet.FindReservationTasklet;

@Configuration
@EnableJdbcJobRepository
@Import(value = { DataSourceConfiguration.class })
public class BatchConfiguration {

	@Bean
	Tasklet reservationTasklet(@Qualifier("batchDataSource") DataSource dataSource) {
		return new FindReservationTasklet(dataSource);
	}

	@Bean
	@StepScope
	Partitioner memberImportStepPartitioner(
			@Value("#{jobExecutionContext['reservations']}") List<Reservation> reservations) {
		return new MemberImportStepPartitioner(reservations);
	}

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
		ItemProcessor<Member, FullNameMember> fullNameItemProcessor = item -> {
			FullNameMember fullNameMember = new FullNameMember();
			fullNameMember.setId(Integer.parseInt(item.getId()));
			fullNameMember.setFirstName(item.getFirstName());
			fullNameMember.setLastName(item.getLastName());
			fullNameMember.setFullName(item.getFirstName() + " " + item.getLastName());

			return fullNameMember;
		};

		return new CompositeItemProcessor<Member, FullNameMember>(
				List.of(beanValidatingItemProcessor(), fullNameItemProcessor));
	}

	@Bean
	ItemWriter<FullNameMember> itemWriter(
			@Qualifier("businessEntityManagerFactory") LocalEntityManagerFactoryBean entityManagerFactoryBean) {
		return new JpaItemWriterBuilder<FullNameMember>().entityManagerFactory(entityManagerFactoryBean.getObject())
				.usePersist(true).build();
	}

	@Bean
	SkipListener<Member, FullNameMember> skipListener() {
		return new MemberItemSkipListener();
	}

	@Bean
	StepExecutionListener stepExecutionListener(@Qualifier("businessDataSource") DataSource dataSource) {
		return new MemberStepExecutionListener(dataSource);
	}

	@Bean("findReservationStep")
	Step tasklet(JobRepository jobRepository, Tasklet tasklet) {
		return new StepBuilder(jobRepository).tasklet(tasklet).build();
	}

	@Bean("masterStep")
	Step masterStep(JobRepository jobRepository, Partitioner partitioner,
			@Qualifier("memberImportStep") Step memberImportSteps) {
		return new StepBuilder(jobRepository).partitioner("memberImportStepPartitioner", partitioner)
				.step(memberImportSteps).gridSize(5).taskExecutor(new VirtualThreadTaskExecutor()).build();
	}

	@Bean("memberImportStep")
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
	Job job(JobRepository jobRepository, @Qualifier("findReservationStep") Step findReservationStep,
			@Qualifier("masterStep") Step masterStep) {
		return new JobBuilder(jobRepository).start(findReservationStep).next(masterStep).build();
	}

}
