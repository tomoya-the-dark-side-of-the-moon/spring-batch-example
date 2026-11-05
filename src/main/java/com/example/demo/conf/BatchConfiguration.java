package com.example.demo.conf;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
	ItemWriter<Member> itemWriter() {
		return chunk -> chunk.forEach(System.out::println);
	}

	@Bean
	Step step(JobRepository jobRepository, ItemReader<Member> itemReader, ItemWriter<Member> itemWriter) {
		return new StepBuilder(jobRepository).<Member, Member>chunk(5).reader(itemReader).writer(itemWriter).build();
	}

	@Bean
	Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder(jobRepository).start(step).build();
	}

}
