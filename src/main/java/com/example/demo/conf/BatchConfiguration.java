package com.example.demo.conf;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.FullNameMember;
import com.example.demo.domain.Member;

@Configuration
public class BatchConfiguration {

	@Bean
	ItemReader<Member> itemReader() {
		return new FlatFileItemReaderBuilder<Member>().name("itemReader")
				.resource(new ClassPathResource("bon_jovi.csv")).delimited().names("id", "firstName", "lastName")
				.linesToSkip(1).targetType(Member.class).build();
	}

	@Bean
	ItemProcessor<Member, FullNameMember> itemProcessor() {
		return item -> new FullNameMember(item.id(), item.firstName(), item.lastName(),
				item.firstName() + " " + item.lastName());
	}

	@Bean
	ItemWriter<FullNameMember> itemWriter() {
		return chunk -> chunk.forEach(System.out::println);
	}

	@Bean
	Step step(JobRepository jobRepository, ItemReader<Member> itemReader,
			ItemProcessor<Member, FullNameMember> itemProcessor, ItemWriter<FullNameMember> itemWriter) {
		return new StepBuilder(jobRepository).<Member, FullNameMember>chunk(5).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter)
				.build();
	}

	@Bean
	Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder(jobRepository).start(step).build();
	}

}
