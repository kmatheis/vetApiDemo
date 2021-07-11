package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.dto.UserDescription;
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.exception.IllegalAttemptException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

public interface UserController {
	
	// Deprecated but remains for reference.
//	@PostMapping( "/loginold" )
//	@ResponseStatus( code = HttpStatus.OK )
//	UserReply loginOld( @RequestBody LoginRequest loginRequest ) throws AuthenticationException;

	@Operation(
		summary = "Returns the logged-in user",
		description = "Returns the logged-in user, should the user provide correct credentials.",
		responses = {
			@ApiResponse( 
				responseCode = "200", description = "The logged-in user is returned", 
				content = @Content( mediaType = "application/json", schema = @Schema( implementation = User.class ) ) ),
			@ApiResponse( 
				responseCode = "401", description = "Password is incorrect", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "404", description = "The user does not exist", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "500", description  = "An unplanned error occurred", 
				content = @Content( mediaType = "application/json" ) )
		}
	)
	@PostMapping( "/users/login" )
	@ResponseStatus( code = HttpStatus.OK )
	ResponseEntity<User> login( @RequestBody LoginRequest loginRequest ) throws AuthenticationException;
	
	@Operation(
		summary = "Returns the list of users",
		description = "Returns the list of users in the system. Only ADMIN role may do this.",
		responses = {
			@ApiResponse( 
				responseCode = "200", description = "The users are returned", 
				content = @Content( mediaType = "application/json", schema = @Schema( ref = "#/components/schemas/UserList" ) ) ), // implementation = User.class
			@ApiResponse( 
				responseCode = "401", description = "User is not authorized to perform this", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "404", description = "The users do not exist", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "500", description  = "An unplanned error occurred", 
				content = @Content( mediaType = "application/json" ) )
		}
	)
	@GetMapping( "/users" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<User> getUsers( @RequestHeader( "Authorization" ) String bearerJwt ) throws AuthenticationException;
	
	@Operation(
		summary = "Returns a list of some users",
		description = "Returns a list of some users in the system whose name contains a given string. Only ADMIN role may do this.",
		responses = {
			@ApiResponse( 
				responseCode = "200", description = "The users are returned", 
				content = @Content( mediaType = "application/json", schema = @Schema( ref = "#/components/schemas/UserList" ) ) ), // implementation = User.class
			@ApiResponse( 
				responseCode = "401", description = "User is not authorized to perform this", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "404", description = "The users do not exist", 
				content = @Content( mediaType = "application/json" ) ),
			@ApiResponse( 
				responseCode = "500", description  = "An unplanned error occurred", 
				content = @Content( mediaType = "application/json" ) )
		}
	)
	@GetMapping( "/someusers" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<User> getSomeUsers( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@RequestParam( required = false ) String nameContains
	) throws AuthenticationException;
	
	@DeleteMapping( "/users/{id}" )
	@ResponseStatus( code = HttpStatus.OK )
	public String deleteUser( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long id 
	) throws AuthenticationException, IllegalAttemptException;
	
	@PostMapping( "/users" )
	@ResponseStatus( code = HttpStatus.OK )
	public String addUser(
			@RequestHeader( "Authorization" ) String bearerJwt,
			@RequestBody UserDescription description
	) throws AuthenticationException;
}
