package com.kmatheis.vet.entity;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kmatheis.vet.Constants;

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
	
	@NotNull( message = "type must exist." )
	private Type type;
	
	@NotNull( message = "comment must exist." )
	@Length( min = Constants.COMMENT_MIN_LENGTH, message = "comment should have at least " + Constants.COMMENT_MIN_LENGTH + " characters." )
	@Length( max = Constants.COMMENT_MAX_LENGTH, message = "comment should have at most " + Constants.COMMENT_MAX_LENGTH + " characters." )
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
