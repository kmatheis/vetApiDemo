package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.dto.Reservation;

public interface ReservationController {

	@GetMapping( "/reservations/animals/{aid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public List<Reservation> getReservationsByAid( 
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long aid 
	) throws AuthenticationException;
	
}
