package com.kmatheis.vet.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty
	public void setPk( Long pk ) {
		this.pk = pk;
	}
	
	@JsonFormat( shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="America/New_York" )
	public Timestamp getOndate() {
		return ondate;
	}
	
	// We don't need to constantly display animalId for comments but we'd still like to deserialize it correctly.
	@JsonIgnore
	public Long getAnimalId() {
		return animalId;
	}
	@JsonProperty
	public void setAnimalId( Long animalId ) {
		this.animalId = animalId;
	}
	
}
