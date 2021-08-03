package com.kmatheis.vet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.UserDao;
import com.kmatheis.vet.dto.UserDescription;
import com.kmatheis.vet.entity.Role;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.exception.IllegalAttemptException;

// import lombok.extern.slf4j.Slf4j;

@Service
// @Slf4j
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthService authService;
	
	public List<User> getUsers( String jwt ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read users" ) );
		authService.authorize( jwt, neededPrivs );
		// log.debug( "getUsers auth is successful!" );
		return userDao.fetchUsers();
	}

	public List<User> getSomeUsers( String jwt, String nameContains ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read users" ) );
		authService.authorize( jwt, neededPrivs );
		// log.debug( "getSomeUsers auth is successful!" );
		return userDao.fetchSomeUsers( nameContains );
	}

	public String deleteUser( String jwt, Long id ) throws AuthenticationException, IllegalAttemptException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "delete users" ) );
		authService.authorize( jwt, neededPrivs );
		// log.debug( "deleteUser auth is successful!" );
		if ( id < 4 ) {
			throw new IllegalAttemptException( "Please do not attempt to delete the initial users." );
		}
		return userDao.deleteUser( id );
	}

	public String addUser( String jwt, UserDescription d ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add users" ) );
		authService.authorize( jwt, neededPrivs );
		// Since the annotations in UserDescription handle all the basic text checks we need, we don't have to have complicated checking code here.
		String rolename = d.getRolename();
		Role role = userDao.fetchRoleByName( rolename ).orElseThrow( () -> new NoSuchElementException( "Role with name " + rolename + " not found." ) );
		return userDao.addUser( d.getUsername(), d.getPassword(), role.getId() );
	}

	public String modifyUser( String jwt, Long id, UserDescription d ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "edit users" ) );
		authService.authorize( jwt, neededPrivs );
		// idem.
		userDao.fetchUserById( id ).orElseThrow( () -> new NoSuchElementException( "User with id " + id + " doesn't exist." ) );  // fetches for confirmation only
		if ( id < 4 ) {
			throw new IllegalAttemptException( "Please do not attempt to modify the initial users." );
		}
		String rolename = d.getRolename();
		Role role = userDao.fetchRoleByName( rolename ).orElseThrow( () -> new NoSuchElementException( "Role with name " + rolename + " not found." ) );
		return userDao.modifyUser( id, d.getUsername(), d.getPassword(), role.getId() );
	}
}
