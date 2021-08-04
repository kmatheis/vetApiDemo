package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.entity.User;

@Validated
public interface ProfileController {

	@GetMapping( "/profiles" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Profile> getProfiles( @RequestHeader( "Authorization" ) String bearerJwt ) throws AuthenticationException;
	
	@GetMapping( "/someprofiles" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Profile> getSomeProfiles( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@RequestParam( required = true ) String nameContains
	) throws AuthenticationException;
	
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
	
	@PostMapping( "/profiles" )
	@ResponseStatus( code = HttpStatus.OK )
	public Profile addProfile(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@Valid @RequestBody Profile profile
	) throws AuthenticationException;
	
	@DeleteMapping( "/profiles/{id}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String deleteProfile( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long id 
	) throws AuthenticationException;
}
