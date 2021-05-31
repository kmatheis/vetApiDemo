package com.kmatheis.vet.entity;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class User {
	private Long id;
	private String username;
	private String hash;
	private Long roleId;     
	
	@JsonIgnore
	public String getHash() {
		return hash;
	}
}

