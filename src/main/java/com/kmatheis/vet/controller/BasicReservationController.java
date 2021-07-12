package com.kmatheis.vet.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.dto.Reservation;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BasicReservationController implements ReservationController {

	@Override
	public List<Reservation> getReservationsByAid( String bearerJwt, Long aid ) throws AuthenticationException {
		
		return null;
	}

}
