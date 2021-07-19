package com.kmatheis.vet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.OwnerDao;
import com.kmatheis.vet.dao.ProfileDao;
import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerService {

	@Autowired
	private OwnerDao ownerDao;
	
	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private AuthService authService;
	
	public List<Owner> getOwnersByPid( String jwt, Long pid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read owners" ) );
		authService.authorize( jwt, neededPrivs );
		Profile p = profileDao.fetchProfileById( pid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + pid + " does not exist." ) );
		return p.getOwners();
	}

	public Owner addOwnerToPid( String jwt, Long pid, Owner owner ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add owners" ) );
		authService.authorize( jwt, neededPrivs );
		Profile p = profileDao.fetchProfileById( pid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + pid + " does not exist." ) );
		
		String name = owner.getName();
		
		if ( name == null || name.length() < 3 ) {
			throw new IllegalAttemptException( "Owner's name must have at least 3 characters." );
		}
		return ownerDao.addOwnerToProfile( name, owner.getPhone(), p );
	}

	private Owner verifyOwner( Long pid, Long oid ) {
		Owner foundOwner = ownerDao.fetchOwnerById( oid ).orElseThrow( () -> new NoSuchElementException( "Owner with id " + oid + " does not exist." ) );
		if ( !foundOwner.getProfileId().equals( pid ) ) {
			throw new NoSuchElementException( "Owner with id " + oid + " does not belong to profile with id " + pid + "." );
		}
		return foundOwner;
	}
	
	public String deleteOwner( String jwt, Long pid, Long oid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "del owners" ) );
		authService.authorize( jwt, neededPrivs );
		verifyOwner( pid, oid );
		return ownerDao.deleteOwner( oid );
	}

	public String modifyOwner( String jwt, Long pid, Long oid, Owner owner ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "edit owners" ) );
		authService.authorize( jwt, neededPrivs );
		Owner foundOwner = verifyOwner( pid, oid );
		String newName = owner.getName();
		if ( newName == null ) {
			newName = foundOwner.getName();
		}
		if ( newName.length() < 3 ) {
			throw new IllegalAttemptException( "Owner's name must have at least 3 characters." );
		}
		
		String newPhone = owner.getPhone();
		if ( newPhone == null ) {
			newPhone = foundOwner.getPhone();
		}
		
		Long newpid = ( owner.getProfileId() == null ? pid : owner.getProfileId() );
		
		Profile newProfile = profileDao.fetchProfileById( newpid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + newpid + " does not exist." ) );  

		return ownerDao.modifyOwner( foundOwner.getPk(), newName, newPhone, newProfile.getPk() );
	}

}
