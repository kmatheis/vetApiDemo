package com.kmatheis.vet.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.kmatheis.vet.Constants;

import lombok.Data;

@Data
public class LoginRequest {
	@NotNull( message = "username must exist." )
	@Length( min = Constants.USERNAME_MIN_LENGTH, message = "username should have at least " + Constants.USERNAME_MIN_LENGTH + " characters." )
	@Length( max = Constants.USERNAME_MAX_LENGTH, message = "username should have at most " + Constants.USERNAME_MAX_LENGTH + " characters." )
	@Pattern( regexp = "[\\w]*", message = "username should be just letters, numbers, and/or underscores." )
	private String username;
	
	@NotNull( message = "password must exist." )
	@Length( min = Constants.USERPW_MIN_LENGTH, message = "password should have at least " + Constants.USERPW_MIN_LENGTH + " characters." )
	@Length( max = Constants.USERPW_MAX_LENGTH, message = "password should have at most " + Constants.USERPW_MAX_LENGTH + " characters." )
	@Pattern( regexp = "[\\w[#?!@$ %^&*-]]*", message = "password should be just letters, numbers, underscores, spaces, and/or certain special characters." )	
	private String password;
}
