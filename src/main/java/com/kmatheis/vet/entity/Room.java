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
public class Room {
	private Long pk;
	private Long id;
	private String name;
	private Long maxcap;
	private Double cost;
	
	@JsonIgnore
	public Long getPk() {
		return pk;
	}
}
