package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.Owner;

public interface OwnerController {
	
	@GetMapping( "/profiles/{pid}/owners" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Owner> getOwnersByPid( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid 
	) throws AuthenticationException;

	@PostMapping( "/profiles/{pid}/owners" )
	@ResponseStatus( code = HttpStatus.OK )
	public Owner addOwnerToPid(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@RequestBody Owner owner
	) throws AuthenticationException;
	
	// Though we don't need /profiles/{pid} as a prefix since we need only the owner's id, we require it since users will make mistakes.
	@DeleteMapping( "/profiles/{pid}/owners/{oid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String deleteOwner(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@PathVariable Long oid
	) throws AuthenticationException;
	
	// idem.
	@PutMapping( "/profiles/{pid}/owners/{oid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String modifyOwner(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@PathVariable Long oid,
			@RequestBody Owner owner
	) throws AuthenticationException;
}
