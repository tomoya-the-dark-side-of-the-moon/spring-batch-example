package com.example.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.example.demo.domain.FullNameMember;
import com.example.demo.domain.Member;

public class MemberItemSkipListener implements SkipListener<Member, FullNameMember> {

	private static final Logger logger = LoggerFactory.getLogger(MemberItemSkipListener.class);

	@Override
	public void onSkipInProcess(Member item, Throwable t) {
		if (t instanceof ValidationException) {
			BindException cause = (BindException) t.getCause();
			for (FieldError fieldError : cause.getFieldErrors()) {
				String field = fieldError.getField();
				String defaultMessage = fieldError.getDefaultMessage();
				logger.warn("{}: {}", field, defaultMessage);
			}
		}
	}

}
