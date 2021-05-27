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

import com.kmatheis.vet.entity.JwtDto;
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.entity.UserReply;

@RequestMapping( "/users" )

public interface UserController {
	
	@PostMapping( "/login" )
	@ResponseStatus( code = HttpStatus.OK )
	UserReply login( @RequestBody LoginRequest loginRequest ) throws AuthenticationException;

	
//	@GetMapping
//	@ResponseStatus( code = HttpStatus.OK )
//	List<User> getUsers( @RequestBody( required = false ) JwtDto jwtdto ) throws AuthenticationException;  // Just need a JWT
	
	@GetMapping
	public ResponseEntity<List<User>> getUsers2( @RequestHeader( "Authorization" ) String jwt ) throws AuthenticationException;
	
}
