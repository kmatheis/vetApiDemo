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

import com.kmatheis.vet.entity.Animal;

public interface AnimalController {

	@GetMapping( "/profiles/{pid}/animals" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Animal> getAnimalsByPid( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid 
	) throws AuthenticationException;
	
	@GetMapping( "/animals/{aid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public Animal getAnimalByAid( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long aid 
	) throws AuthenticationException;
	
	@PostMapping( "/profiles/{pid}/animals" )
	@ResponseStatus( code = HttpStatus.OK )
	public Animal addAnimalToPid(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@RequestBody Animal animal
	) throws AuthenticationException;
	
	// Though we don't need /profiles/{pid} as a prefix since we need only the animal's id, we require it since users will make mistakes.
	@DeleteMapping( "/profiles/{pid}/animals/{aid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String deleteAnimal(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@PathVariable Long aid
	) throws AuthenticationException;
	
	// idem.
	@PutMapping( "/profiles/{pid}/animals/{aid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String modifyAnimal(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long pid,
			@PathVariable Long aid,
			@RequestBody Animal animal
	) throws AuthenticationException;
}
