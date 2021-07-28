package com.kmatheis.vet.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class Comment {
	private Long pk;
	private Long id;
	private Timestamp ondate;
	private Type type;
	private String comment;
	private Long animalId;
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
	
	@JsonFormat( shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="America/New_York" )
	public Timestamp getOndate() {
		return ondate;
	}
	
}
