package com.kmatheis.vet.service;

import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.UserDao;
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.entity.UserReply;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
@Slf4j
public class AuthService {

	@Autowired
	private UserDao userDao;
	
	public UserReply login( LoginRequest loginRequest ) throws AuthenticationException {
		String username = loginRequest.getUsername();
		User foundUser = userDao.fetchUser( username )
				.orElseThrow( () -> new NoSuchElementException( "Cannot find user with username " + username ) );
		
		if ( BCrypt.checkpw( loginRequest.getPassword(), foundUser.getHash() ) ) {
			log.debug( "User {} found!", username );
			return null;
		}
		
		throw new AuthenticationException( "Incorrect username or password." );
	}
}
