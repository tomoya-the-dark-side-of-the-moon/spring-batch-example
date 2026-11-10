package com.example.demo.conf;

import java.util.ArrayList;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.batch.core.scope.context.StepContext;

import com.example.demo.domain.Member;
import com.example.demo.domain.Reservation;

public class BatchRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		// DataClassRowMapper / BeanWrapperFieldSetMapper / Bean Validation 用
		hints.reflection().registerType(Reservation.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
				MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.ACCESS_DECLARED_FIELDS);
		hints.reflection().registerType(Member.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
				MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.ACCESS_DECLARED_FIELDS);

		// @StepScope の SpEL (#{jobExecutionContext[...]}, #{stepExecutionContext[...]}) 用
		hints.reflection().registerType(StepContext.class, MemberCategory.INVOKE_PUBLIC_METHODS);

		// FlatFileItemReader が読み込む classpath 上の CSV ファイル
		hints.resources().registerPattern("*.csv");

		// ExecutionContext の Java シリアライズ用
		hints.reflection().registerJavaSerialization(Reservation.class);
		hints.reflection().registerJavaSerialization(ArrayList.class);
		//hints.serialization().registerType(Reservation.class);
		//hints.serialization().registerType(ArrayList.class);
	}

}
