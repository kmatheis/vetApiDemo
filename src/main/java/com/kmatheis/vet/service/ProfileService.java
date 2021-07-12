package com.kmatheis.vet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.ProfileDao;
import com.kmatheis.vet.entity.Profile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfileService {
	
	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private AuthService authService;

	public List<Profile> getProfiles( String jwt ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read profiles" ) );
		authService.authorize( jwt, neededPrivs );
		return profileDao.fetchProfiles();
	}

	public Profile getProfile( String jwt, Long id ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read profiles" ) );
		authService.authorize( jwt, neededPrivs );
		Profile p = profileDao.fetchProfileById( id ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + id + " does not exist." ) );
		return p;
	}
	
}
