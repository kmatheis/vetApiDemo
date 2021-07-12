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
import com.kmatheis.vet.exception.IllegalAttemptException;

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

	public String modifyProfile( String jwt, Long id, Profile profile ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "edit profiles" ) );
		authService.authorize( jwt, neededPrivs );
		// System.out.println( profile );  // Reads all data in correctly, nullifying absent values (such as pk, owner profile, animal profile, etc).
		// Need policies: 
		//   1) Do we ignore profile id in data but accept animal/owner ids? 
		//   2) Do we flatly reject all data with an id field in it?
		//      If so, and if we accept compound changes, we either scram and recreate animals/owners each time, or we match animals/owners to names.
		//   3) May make the most sense to reject compound changes and just take simple changes, relying on other endpoints for attaching/deleting.
		// Going with Option (3):
		String name = profile.getName();
		if ( name == null || name.length() < 6 ) {
			throw new IllegalAttemptException( "Profile name must have at least 6 characters." );
		}
		return profileDao.modifyProfile( id, name );
	}
	
}
