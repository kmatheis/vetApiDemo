package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.service.AuthService;
import com.kmatheis.vet.service.ProfileService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicProfileController implements ProfileController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private ProfileService profileService;

	@Override
	public List<Profile> getProfiles( String bearerJwt ) throws AuthenticationException {
		return profileService.getProfiles( authService.bearerToJwt( bearerJwt ) );
	}

	@Override
	public Profile getProfile( String bearerJwt, Long id ) throws AuthenticationException {
		return profileService.getProfile( authService.bearerToJwt( bearerJwt ), id );
	}

	@Override
	public String modifyProfile( String bearerJwt, Long id, Profile profile ) throws AuthenticationException {
		return profileService.modifyProfile( authService.bearerToJwt( bearerJwt ), id, profile );
	}
	
}
