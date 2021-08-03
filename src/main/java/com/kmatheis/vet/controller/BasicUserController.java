package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.dto.LoginRequest;
import com.kmatheis.vet.dto.UserDescription;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.exception.IllegalAttemptException;
import com.kmatheis.vet.internal.UserReply;
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
	
	// Deprecated but remains for reference.
//	@Override
//	public UserReply loginOld( LoginRequest loginRequest ) throws AuthenticationException {
//		// log.debug( "LoginRequest={}", loginRequest );
//		return authService.login( loginRequest );
//	}

	@Override
	public ResponseEntity<User> login( LoginRequest loginRequest ) throws AuthenticationException {
		// log.debug( "LoginRequest={}", loginRequest );
		UserReply ur = authService.login( loginRequest ); 
		HttpHeaders headers = new HttpHeaders();
		headers.add( "Authorization", ur.getJwt() );
		return new ResponseEntity<User>( ur.getUser(), headers, HttpStatus.OK );
	}
	
	@Override
	public List<User> getUsers( @RequestHeader( "Authorization" ) String bearerJwt ) throws AuthenticationException {
		// log.debug( "In getUsers, Authorization header is {}", bearerJwt );
		List<User> users = userService.getUsers( authService.bearerToJwt( bearerJwt ) );
		return users;
	}

	@Override
	public List<User> getSomeUsers( String bearerJwt, String nameContains ) throws AuthenticationException {
		// log.debug( "In getSomeUsers, Authorization header is {}", bearerJwt );
		List<User> users = userService.getSomeUsers( authService.bearerToJwt( bearerJwt ), nameContains );
		return users;
	}
	
	@Override
	public String deleteUser( String bearerJwt, Long id ) throws AuthenticationException, IllegalAttemptException {
		// log.debug( "In deleteUser, Authorization header is {}", bearerJwt );
		return userService.deleteUser( authService.bearerToJwt( bearerJwt ), id );
	}

	@Override
	public String addUser( String bearerJwt, UserDescription description ) throws AuthenticationException {
		// log.debug( "In addUser, Authorization header is {}", bearerJwt );
		return userService.addUser( authService.bearerToJwt( bearerJwt ), description );
	}

	@Override
	public String modifyUser( String bearerJwt, Long id, UserDescription description ) throws AuthenticationException {
		return userService.modifyUser( authService.bearerToJwt( bearerJwt ), id, description );
	}

}
