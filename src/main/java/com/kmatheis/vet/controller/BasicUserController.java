package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.JwtDto;
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.entity.UserReply;
import com.kmatheis.vet.service.AuthService;
import com.kmatheis.vet.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicUserController implements UserController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public UserReply login( LoginRequest loginRequest ) throws AuthenticationException {
		// log.debug( "LoginRequest={}", loginRequest );
		return authService.login( loginRequest );
	}
	
//	@Override
//	public List<User> getUsers( JwtDto jwtdto ) throws AuthenticationException {
//		log.debug( "In getUsers, input is {}", jwtdto.getJwt() );
//		return userService.getUsers( jwtdto.getJwt() );
//	}
	
	public ResponseEntity<List<User>> getUsers2( @RequestHeader( "Authorization" ) String bearerJwt ) throws AuthenticationException {
		log.debug( "In getUsers2, Authorization header is {}", bearerJwt );
		List<User> users = userService.getUsers( bearerJwt.substring( bearerJwt.indexOf( " " ) + 1 ) );  // clean this up later
		return new ResponseEntity<List<User>>( users, HttpStatus.OK );
	}
}
