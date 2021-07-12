package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.Profile;

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
}
