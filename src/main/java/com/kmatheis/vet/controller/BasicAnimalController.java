package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.service.AnimalService;
import com.kmatheis.vet.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicAnimalController implements AnimalController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AnimalService animalService;
	
	@Override
	public List<Animal> getAnimalsByPid( String bearerJwt, Long pid ) throws AuthenticationException {
		return animalService.getAnimalsByPid( authService.bearerToJwt( bearerJwt ), pid );
	}

	@Override
	public Animal addAnimalToPid( String bearerJwt, Long pid, Animal animal ) throws AuthenticationException {
		return animalService.addAnimalToPid( authService.bearerToJwt( bearerJwt ), pid, animal );
	}

	@Override
	public String deleteAnimal( String bearerJwt, Long pid, Long aid ) throws AuthenticationException {
		return animalService.deleteAnimal( authService.bearerToJwt( bearerJwt ), pid, aid );
	}

	@Override
	public String modifyAnimal( String bearerJwt, Long pid, Long aid, Animal animal ) throws AuthenticationException {
		return animalService.modifyAnimal( authService.bearerToJwt( bearerJwt ), pid, aid, animal );
	}

}
