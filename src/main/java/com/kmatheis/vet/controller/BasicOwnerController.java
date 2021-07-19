package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.service.AuthService;
import com.kmatheis.vet.service.OwnerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicOwnerController implements OwnerController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private OwnerService ownerService;
	
	@Override
	public List<Owner> getOwnersByPid( String bearerJwt, Long pid ) throws AuthenticationException {
		return ownerService.getOwnersByPid( authService.bearerToJwt( bearerJwt ), pid );
	}

	@Override
	public Owner addOwnerToPid( String bearerJwt, Long pid, Owner owner ) throws AuthenticationException {
		return ownerService.addOwnerToPid( authService.bearerToJwt( bearerJwt ), pid, owner );
	}

	@Override
	public String deleteOwner( String bearerJwt, Long pid, Long oid ) throws AuthenticationException {
		return ownerService.deleteOwner( authService.bearerToJwt( bearerJwt ), pid, oid );
	}

	@Override
	public String modifyOwner( String bearerJwt, Long pid, Long oid, Owner owner ) throws AuthenticationException {
		return ownerService.modifyOwner( authService.bearerToJwt( bearerJwt ), pid, oid, owner );
	}
	
}
