package com.kmatheis.vet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class Animal {
	private Long pk;
	private Long id;
	private String name;
	private Species species;
	private Profile profile;
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
	
	@JsonIgnore
	public Profile getProfile() {  // to avoid cyclic display issues, we hide the display of the Profile
		return profile;
	}
}
