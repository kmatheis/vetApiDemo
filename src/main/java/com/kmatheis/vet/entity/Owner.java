package com.kmatheis.vet.entity;

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
public class Owner {
	private Long pk;
	private Long id;
	
	// name could be null in the case of a PUT
	@Length( min = Constants.OWNERNAME_MIN_LENGTH, message = "name should have at least " + Constants.OWNERNAME_MIN_LENGTH + " characters." )
	@Length( max = Constants.OWNERNAME_MAX_LENGTH, message = "name should have at most " + Constants.OWNERNAME_MAX_LENGTH + " characters." )
	@Pattern( regexp = "^[a-zA-Z][a-zA-Z ]*[a-zA-Z]$", message = "name should be just letters with possibly spaces in between." )
	private String name;
	
	// phone can always be null
	@Length( max = Constants.OWNERPHONE_MAX_LENGTH, message = "phone should have at most " + Constants.OWNERPHONE_MAX_LENGTH + " characters." )
	private String phone;
	
	// private Profile profile;  // best if we don't do this for cyclic reasons (e.g., System.out.println( profile ) )
	private Long profileId;  // should still know to which profile the owner is attached though
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
	
//	@JsonIgnore
//	public Profile getProfile() {  // to avoid cyclic display issues, we hide the display of the Profile
//		return profile;
//	}
}
