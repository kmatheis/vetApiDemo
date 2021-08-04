package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.dto.Reservation;

@Validated
public interface ReservationController {

	@GetMapping( "/reservations/animals/{aid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Reservation> getReservationsByAid( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long aid 
	) throws AuthenticationException;
	
	@PostMapping( "/reservations" )
	@ResponseStatus( code = HttpStatus.OK )
	public String addReservation(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@Valid @RequestBody Reservation res
	) throws AuthenticationException;
}
