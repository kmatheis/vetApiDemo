package com.kmatheis.vet.entity;

import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kmatheis.vet.Constants;

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
	
	// can be null if the animal is modified (e.g., moved to a different profile)
	@Length( min = Constants.ANIMALNAME_MIN_LENGTH, message = "name should have at least " + Constants.ANIMALNAME_MIN_LENGTH + " characters." )
	@Length( max = Constants.ANIMALNAME_MAX_LENGTH, message = "name should have at most " + Constants.ANIMALNAME_MAX_LENGTH + " characters." )
	@Pattern( regexp = "[\\w]*", message = "name should be either null or at least three letters, numbers, and/or underscores." )
	private String name;
	
	private Species species;
	// private Profile profile;  // best if we don't do this for cyclic reasons (e.g., System.out.println( profile ) )
	private Long profileId;  // should still know to which profile the animal is attached though
	private List<Comment> comments;
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
	
//	@JsonIgnore
//	public Profile getProfile() {  // to avoid cyclic display issues, we hide the display of the Profile
//		return profile;
//	}
}
