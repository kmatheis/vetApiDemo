package com.kmatheis.vet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.UserDao;
import com.kmatheis.vet.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthService authService;
	
	public List<User> getUsers( String jwt ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read users" ) );
		authService.authorize( jwt, neededPrivs );
		log.debug( "getUsers auth is successful!" );
		return userDao.fetchUsers();
	}

	public List<User> getSomeUsers( String jwt, String nameContains ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read users" ) );
		authService.authorize( jwt, neededPrivs );
		log.debug( "getSomeUsers auth is successful!" );
		return userDao.fetchSomeUsers( nameContains );
	}
}
