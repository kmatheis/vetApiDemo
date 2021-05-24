package com.kmatheis.vet.controller;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.UserReply;

@RequestMapping( "/users" )

public interface UserController {
	
	@PostMapping( "/login" )
	@ResponseStatus( code = HttpStatus.OK )
	UserReply login( @RequestBody LoginRequest loginRequest ) throws AuthenticationException;

}
