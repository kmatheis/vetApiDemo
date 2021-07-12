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
		log.debug( "in addAnimalToPid, animal obtained from user is {}", animal );
		String name = animal.getName();
		Species species = animal.getSpecies();
		
		// Not sure how it serializes if user enters wrong enum value, so possibly check species here
		System.out.println( animal );
		if ( name == null || name.length() < 3 ) {
			throw new IllegalAttemptException( "Animal's name must have at least 3 characters." );
		}
		return animalDao.addAnimalToProfile( name, species, p );
	}
	
}
