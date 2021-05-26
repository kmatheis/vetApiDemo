package com.kmatheis.vet.service;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.kmatheis.vet.dao.UserDao;
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.Role;
import com.kmatheis.vet.entity.ServerKey;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.entity.UserReply;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
@Slf4j
public class AuthService {

	@Autowired
	private UserDao userDao;
	
	private void setWorkingKey() throws AuthenticationException {
		List<ServerKey> keys = userDao.fetchServerKeys();
		ServerKey sk;
		
		if ( keys.isEmpty() ) {
			log.error( "There is (probably) no server key in the database." );
			throw new AuthenticationException( "No server key found!" );
		}
		
		sk = keys.get( 0 );
		byte[] byteKey = Base64.getDecoder().decode( sk.getServerKey() );
		ServerKey.workingKey = Keys.hmacShaKeyFor( byteKey );
	}
	
	public UserReply login( LoginRequest loginRequest ) throws AuthenticationException {
		String username = loginRequest.getUsername();
		User foundUser = userDao.fetchUser( username )
				.orElseThrow( () -> new NoSuchElementException( "Cannot find user with username " + username ) );
		
		if ( BCrypt.checkpw( loginRequest.getPassword(), foundUser.getHash() ) ) {
			log.debug( "User {} found!", username );

			if ( ServerKey.workingKey == null ) {
				setWorkingKey();
			}
			
			Role role = userDao.fetchRole( foundUser.getRoleId() );
			
			List<String> privList = userDao.fetchPrivs( foundUser.getRoleId() );
			
			String privs = String.join( ",", privList );
			
			String jwt = Jwts.builder()
							.claim( "role", role.getRolename() )
							.claim( "privs", privs )
							.setSubject( "VET API DEMO" )
							.setExpiration( new Date( System.currentTimeMillis() + 3600000 ) )  // One hour
							.signWith( ServerKey.workingKey )
							.compact();
			
			log.debug( "jwt returned is {}", jwt );
			
			UserReply out = UserReply.builder()
							.user( foundUser )
							.jwt( jwt )
							.build();
			
			return out;
		}
		
		throw new AuthenticationException( "Incorrect username or password." );
	}
}
