package com.kmatheis.vet.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.UserReply;
import com.kmatheis.vet.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicUserController implements UserController {

	@Autowired
	private AuthService authService;
	
	@Override
	public UserReply login( LoginRequest loginRequest ) throws AuthenticationException {
		log.debug( "LoginRequest={}", loginRequest );
		// try {
		return authService.login( loginRequest );
		// } catch ( Exception e ) {
		// 	return null;
		// }
	}
}
