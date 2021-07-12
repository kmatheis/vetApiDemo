package com.kmatheis.vet.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.AnimalDao;
import com.kmatheis.vet.dao.ProfileDao;
import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.entity.Species;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AnimalService {

	@Autowired
	private AnimalDao animalDao;
	
	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private AuthService authService;

	public List<Animal> getAnimalsByPid( String jwt, Long pid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read animals" ) );
		authService.authorize( jwt, neededPrivs );
		Profile p = profileDao.fetchProfileById( pid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + pid + " does not exist." ) );
		return p.getAnimals();
	}

	public Animal addAnimalToPid( String jwt, Long pid, Animal animal ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add animals" ) );
		authService.authorize( jwt, neededPrivs );
		Profile p = profileDao.fetchProfileById( pid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + pid + " does not exist." ) );
		// log.debug( "in addAnimalToPid, animal obtained from user is {}", animal );
		String name = animal.getName();
		Species species = animal.getSpecies();
		// HttoMessageNotReadableException will occur if species is not one of the predefined DB Enums, so we just add that to the 
		//   GlobalErrorHandler and not do any checking here.
		if ( name == null || name.length() < 3 ) {
			throw new IllegalAttemptException( "Animal's name must have at least 3 characters." );
		}
		return animalDao.addAnimalToProfile( name, species, p );
	}

	private Animal verifyAnimal( Long pid, Long aid ) {
		Animal foundAnimal = animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
		// log.debug( "in verifyAnimal, animal retrieved is {}", foundAnimal );
		if ( !foundAnimal.getProfileId().equals( pid ) ) {
			throw new NoSuchElementException( "Animal with id " + aid + " does not belong to profile with id " + pid + "." );
		}
		return foundAnimal;
	}
	
	public String deleteAnimal( String jwt, Long pid, Long aid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "del animals" ) );
		authService.authorize( jwt, neededPrivs );
		verifyAnimal( pid, aid );
		return animalDao.deleteAnimal( aid );
	}

	// facilitates transfer of animal from one profile to another (possibly allowing a name change)
	public String modifyAnimal( String jwt, Long pid, Long aid, Animal animal ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "edit animals" ) );
		authService.authorize( jwt, neededPrivs );
		Animal foundAnimal = verifyAnimal( pid, aid );
		String newName = animal.getName();
		if ( newName == null ) {
			newName = foundAnimal.getName();
		}
		if ( newName.length() < 3 ) {
			throw new IllegalAttemptException( "Animal's name must have at least 3 characters." );
		}
		Long newpid = animal.getProfileId();
		Profile newProfile = profileDao.fetchProfileById( newpid ).orElseThrow( () -> new NoSuchElementException( "Profile with id " + newpid + " does not exist." ) );  

		return animalDao.modifyAnimal( foundAnimal.getPk(), newName, newProfile.getPk() );
	}
	
}
