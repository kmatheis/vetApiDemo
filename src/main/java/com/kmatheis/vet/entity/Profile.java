package com.kmatheis.vet.entity;

import java.util.List;

import javax.validation.constraints.NotNull;
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
public class Profile {
	private Long pk;
	private Long id;
	
	@NotNull( message = "name must exist." )
	@Length( min = Constants.PROFILENAME_MIN_LENGTH, message = "name should have at least " + Constants.PROFILENAME_MIN_LENGTH + " characters." )
	@Length( max = Constants.PROFILENAME_MAX_LENGTH, message = "name should have at most " + Constants.PROFILENAME_MAX_LENGTH + " characters." )
	@Pattern( regexp = "[\\w[#?!@$ %^&*-]]*", message = "name should be just letters, numbers, underscores, spaces, and/or certain special characters." )
	private String name;
	
	private List<Owner> owners;
	private List<Animal> animals;
	
	@JsonIgnore
	public Long getPk() {  // for security, we hide the pk
		return pk;
	}
}
