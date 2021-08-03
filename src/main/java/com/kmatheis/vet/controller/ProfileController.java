package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.Profile;

@Validated
public interface ProfileController {

	@GetMapping( "/profiles" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Profile> getProfiles( @RequestHeader( "Authorization" ) String bearerJwt ) throws AuthenticationException;
	
	@GetMapping( "/profiles/{id}" )
	@ResponseStatus( code = HttpStatus.OK )
	public Profile getProfile( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long id 
	) throws AuthenticationException;
	
	@PutMapping( "/profiles/{id}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String modifyProfile(
			@RequestHeader( "Authorization" ) String bearerJwt,
			@PathVariable Long id,
			@Valid @RequestBody Profile profile
	) throws AuthenticationException;
}
