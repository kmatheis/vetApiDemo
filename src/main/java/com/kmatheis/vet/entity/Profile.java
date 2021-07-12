package com.kmatheis.vet.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class Profile {
	private Long pk;
	private Long id;
	private String name;
	private List<Owner> owners;
	private List<Animal> animals;
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
}
