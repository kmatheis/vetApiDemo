package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.dto.Reservation;
import com.kmatheis.vet.service.AnimalService;
import com.kmatheis.vet.service.AuthService;
import com.kmatheis.vet.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicReservationController implements ReservationController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Override
	public List<Reservation> getReservationsByAid( String bearerJwt, Long aid ) throws AuthenticationException {
		return reservationService.getReservationsByAid( authService.bearerToJwt( bearerJwt ), aid );
	}

	@Override
	public String addReservation( String bearerJwt, Reservation res ) throws AuthenticationException {
		return reservationService.addReservation( authService.bearerToJwt( bearerJwt ), res );
	}

}
