package com.example.demo.domain;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public class Member {

	@NotBlank
	private String id;

	@NotBlank
	@Length(max = 20)
	private String firstName;

	@NotBlank
	@Length(max = 20)
	private String lastName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
