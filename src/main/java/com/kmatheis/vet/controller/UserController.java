package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RequestMapping( "/users" )

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
	@PostMapping( "/login" )
	@ResponseStatus( code = HttpStatus.OK )
	ResponseEntity<User> login( @RequestBody LoginRequest loginRequest ) throws AuthenticationException;
	
	@Operation(
		summary = "Returns the list of users",
		description = "Returns the list of users in the system. Only ADMIN role may do this.",
		responses = {
			@ApiResponse( 
				responseCode = "200", description = "The users are returned", 
				content = @Content( mediaType = "application/json", schema = @Schema( implementation = User.class ) ) ),
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
	@GetMapping
	public List<User> getUsers( @RequestHeader( "Authorization" ) String jwt ) throws AuthenticationException;
	
}
