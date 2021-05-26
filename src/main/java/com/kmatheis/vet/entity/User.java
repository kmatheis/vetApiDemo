package com.kmatheis.vet.entity;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
	private Long id;
	private String username;
	private String hash;
	private Long roleId;     
	
	private String jwt;
	
	@Transient
	@JsonIgnore
	public String getJwt() {
		return jwt;
	}
}

